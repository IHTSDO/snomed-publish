#!/usr/local/bin/perl
#-------------------------------------------------------------------------------
# Copyright IHTSDO 2012
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#       http://www.apache.org/licenses/LICENSE-2.0
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
#-------------------------------------------------------------------------------
# This perl script carries no warranty of fitness for any purpose.
# Use at your own risk.
#-------------------------------------------------------------------------------
# This perl script computes and outputs the transitive closure history
# for the SNOMED CT relationships table.
# For RF1, it is the usual (distribution normal form) relationships table.
# For RF2, it is the snapshot relationships table.
# The first row in the file is used to determine whether it is RF1 or RF2
#    relying on the first column of the first row to be "RELATIONSHIPID" in RF1,
#    and "id" in RF2.
# Isa relationships are those elements in the table with relationshipID=116680003
#    In RF1, the columns for c1-rel-c2 are 1,2,3 (first column is zero'th)
#            and characteristicType is column 4.  Defining = 0.
#    In RF2, the columns for c1-rel-c2 are 4,7,5
#            and characteristicType is column 8.  Defining = 900000000000011006
#-------------------------------------------------------------------------------

# use this script as
# perl transitiveClosure.pl <relationshipsFileName> <outputFileName>
# output is a tab-delimited file with two columns, child - parent.

#-------------------------------------------------------------------------------
# Start MAIN
#-------------------------------------------------------------------------------


# get all the file names into a list
my $inputFileName = shift; # the first argument is the name of the input relationships file.
my $outputFileName = shift; # the second argument is the output file name


# for baseline file: read, compute baseline transitive closure


%children = ();
%visited = ();
%descendants = (); 

&readrels(\%children,$inputFileName);

$counter=0;
$root="138875005";


transClos($root,\%children,\%descendants,\%visited);

printRels(\%descendants,$outputFileName);


#-------------------------------------------------------------------------------
# END MAIN
#-------------------------------------------------------------------------------

#-------------------------------------------------------------------------------
# INPUT
#-------------------------------------------------------------------------------
# Takes as arguments: a hash table reference and an argument number $argn
# Opens the relationships table in the file designated by the name in $ARGV[$argn]
# Reads the isa-s and stores them in the hash 
#-------------------------------------------------------------------------------
sub readrels {
   local($childhashref,$inputFilename) = @_;
   my ($firstline,@values);
   open(ISAS,$inputFilename) || die "can't open $inputFilename";
   # read first input row
   $firstline = <ISAS>;
   # first row contains the column names
   @values = split('\t',$firstline);
   if ($values[0] eq "id") { # RF2 input
            # read remaining input rows
         while (<ISAS>) {
            chop;
            @values=split('\t',$_);
            if (($values[7] eq "116680003") && ($values[8] eq "900000000000011006") && ($values[2] eq "1")) { # rel.Type is "is-a", char.Type is "defining", rel is active
               $$childhashref{$values[5]}{$values[4]} = 1; # a hash of hashes, where parent is 1st arg and child is 2nd.
            }
         }
      } elsif ($values[0] eq "RELATIONSHIPID") { # RF1 input
         # read remaining input rows
         while (<ISAS>) {
            chop;
            @values=split('\t',$_);
            if (($values[2] eq "116680003") && ($values[4] eq "0")) { # rel.Type is "is-a", char.Type is "defining". No inactive defining rels are in the RF1 files.
               $$childhashref{$values[3]}{$values[1]} = 1; # a hash of hashes, where parent is 1st arg and child is 2nd.
            }
         }
      } else { print "First line of $inputFilename does not appear to be either RF1 or RF2 format.\n"; }
   close(ISAS);
}


#-------------------------------------------------------------------------------
# transClos
#-------------------------------------------------------------------------------
# This subroutine is based on a method described in "Transitive Closure Algorithms
# Based on Graph Traversal" by Yannis Ioannidis, Raghu Ramakrishnan, and Linda Winger,
# ACM Transactions on Database Systems, Vol. 18, No. 3, September 1993,
# Pages: 512 - 576.
# It uses a simplified version of their "DAG_DFTC" algorithm.
#-------------------------------------------------------------------------------
# 
sub transClos { # recursively depth-first traverse the graph.
   local($startnode,$children,$descendants,$visited) = @_;
   my($descendant, $childnode);
   $counter++;
   # if (($counter % 1000) eq 0) { print "Visit ", $startnode, " ", $counter, "\n"; }
   for $childnode (keys %{ $$children{$startnode} }) { # for all the children of the startnode
       unless ($$visited{$childnode}) {  # unless it has already been traversed
          &transClos($childnode,$children,$descendants,$visited); # recursively visit the childnode
          $$visited{$childnode}="T"; # and when the recursive visit completes, mark as visited
       } # end unless
       for $descendant (keys %{ $$descendants{$childnode} }) { # for each descendant of childnode
          $$descendants{$startnode}{$descendant} = 1; # mark as a descendant of startnode
       }
       $$descendants{$startnode}{$childnode} = 1; # mark the immediate childnode as a descendant of startnode
   } # end for
} # end sub transClos


#-------------------------------------------------------------------------------
# OUTPUT
#-------------------------------------------------------------------------------

sub printRels {
   local($descendants,$outFileName)=@_;
   open(OUTF,">$outFileName") || die "can't open $outFileName";
   for $startnode (keys %$descendants) {
      for $endnode ( keys %{ $$descendants{$startnode} }) {
         print OUTF "$endnode\t$startnode\n";
      }
#      print OUTF "\n";
   }
}


#-------------------------------------------------------------------------------
# END
#-------------------------------------------------------------------------------


