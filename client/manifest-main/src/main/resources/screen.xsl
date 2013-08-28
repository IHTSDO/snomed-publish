<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns:sn="http://ihtsdo.org/snomed/schema/manifest/v1" 
    xmlns="http://www.w3.org/1999/xhtml"  version="1.0">
    
    <xsl:output method="html" encoding="utf-8" indent="yes"/>    
    <xsl:strip-space elements="*"/>
    <xsl:template match="/sn:manifest">
        <xsl:variable name="path" select="'.'"/>
        <html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:sn="http://ihtsdo.org/snomed/schema/manifest/v1">
        <head>
            <title>Release Manifest for _ROOT_NAME_</title>
            <meta charset="utf-8"/>
            <meta name="description" content="Snomed browser"/>
            <meta name="author" content="Henrik Pettersen, Sparkling Ideas"/>
            <link rel="stylesheet" href="Manifest/screen.css?v=1.0"/>
            <script type="text/javascript">
            function changeOntology(value) {
                var redirect;
                redirect = "/ontology/" + value + "/concept/";
                document.location.href = redirect;
            }
            </script>
        </head>
        <body>
            <div class="intro">
                <p>The SNOMED CT International Release is provided in UTF-8 encoded tab-delimited flat files which can be imported into any database or other software application.  SNOMED CT is not software.</p>
                <p>The SNOMED CT files are designed as relational tables with each line in the file representing a row in the table. The first row of each table contains column headings. All other rows contain data.</p>
                <p>The SNOMED CT International Release is delivered to IHTSDO Member National Centers and authorized Affiliate Licensees via Internet download.</p>
                <p>© 2002-2013 The International Health Terminology Standards Development Organisation (IHTSDO).  All Rights Reserved.  SNOMED CT® was originally created by The College of American Pathologists.  "SNOMED" and "SNOMED CT" are registered trademarks of the IHTSDO.</p>
                <p>SNOMED CT has been created by combining SNOMED RT and a computer based nomenclature and classification known as Clinical Terms Version 3, formerly known as Read Codes Version 3, which was created on behalf of the UK Department of Health and is Crown copyright.</p>
                <p style="font-weight: bold">In this release:</p>
                <p>The following SNOMED CT descriptions have no corresponding en-us or en-gb language reference set members. It is permissible for descriptions not to be referenced in language reference sets, and consequently this is not an error.</p> 
                <p>This issue affects the January and July 2013 releases, in the Release Format 1 and Release Format 2 files.</p>
                <p>SNOMED CT releases prior to January 2013 established the precedent that descriptions were referenced in at least one language reference set. Therefore the omissions of descriptions from the language reference sets will be reviewed by the Chief Terminologist and, if necessary, the descriptions will be added to the appropriate language reference sets in the next authoring cycle.</p>
                <div id="templist">
                    <pre style="font-weight: bold">
conceptid       id          term
                    </pre>
                    <pre>
conceptid       id          term
456864006       2921243018  Fox squirrel
456866008       2921245013  Eastern fox squirrel
457592003       2921544013  Obstruction of coronary sinus
229035004       2923148011  MV - Megavolt
609469009       2966715014  Termination of pregnancy complicated by laceration of cervix
291591000119107 2959496014  Spontaneous subacute subdural hemorrhage
609466002       2966717018  Termination of pregnancy complicated by intravascular hemolysis
609486003       2966721013  Termination of pregnancy complicated by septic embolism
609489005       2966724017  Termination of pregnancy complicated by soap embolism
609473007       2966725016  Termination of pregnancy complicated by parametritis
609477008       2966727012  Termination of pregnancy complicated by perforation of uterus
609485004       2966729010  Termination of pregnancy complicated by sepsis
609471009       2966735010  Termination of pregnancy complicated by laceration of vagina
609460008       2966738012  Termination of pregnancy complicated by cardiac arrest and/or failure
609467006       2966739016  Termination of pregnancy complicated by laceration of bowel
609483006       2966741015  Termination of pregnancy complicated by salpingitis
609449004       2966747016  Termination of pregnancy complicated by embolism
609482001       2966748014  Termination of pregnancy complicated by renal tubular necrosis
609494005       2966750018  Termination of pregnancy complicated by pelvic disorder
609468001       2966751019  Termination of pregnancy complicated by laceration of broad ligament
609457001       2966752014  Termination of pregnancy complicated by air embolism
609487007       2966756012  Termination of pregnancy complicated by septic shock
609466002       2966758013  Termination of pregnancy complicated by intravascular haemolysis
609458006       2966759017  Termination of pregnancy complicated by amniotic fluid embolism
609465003       2966761014  Termination of pregnancy complicated by fat embolism
609455009       2966764018  Termination of pregnancy complicated by acute renal failure
609488002       2966768015  Termination of pregnancy complicated by septicemia
609474001       2966773014  Termination of pregnancy complicated by pelvic peritonitis
609475000       2966776018  Termination of pregnancy complicated by perforation of bowel
156072005       2966442014  Incomplete spontaneous abortion
609459003       2966777010  Termination of pregnancy complicated by blood-clot embolism
609484000       2966780011  Termination of pregnancy complicated by salpingo-oophoritis
609490001       2966782015  Termination of pregnancy complicated by uraemia
609448007       2966783013  Termination of pregnancy complicated by delayed and/or excessive hemorrhage
609461007       2966784019  Termination of pregnancy complicated by cerebral anoxia
609448007       2966786017  Termination of pregnancy complicated by delayed and/or excessive haemorrhage
609479006       2966789012  Termination of pregnancy complicated by postoperative shock
609456005       2966793018  Termination of pregnancy complicated by afibrinogenemia
609463005       2966794012  Termination of pregnancy complicated by electrolyte imbalance
609470005       2966796014  Termination of pregnancy complicated by laceration of uterus
609490001       2966797017  Termination of pregnancy complicated by uremia
609450004       2966798010  Termination of pregnancy complicated by genital-pelvic infection
609456005       2966799019  Termination of pregnancy complicated by afibrinogenaemia
609488002       2966802011  Termination of pregnancy complicated by septicaemia
609447002       2966803018  Termination of pregnancy complicated by damage to pelvic organs and/or tissues
609464004       2966805013  Termination of pregnancy complicated by endometritis
609476004       2966806014  Termination of pregnancy complicated by perforation of cervix
609491002       2966807017  Termination of pregnancy complicated by urinary tract infection
609453002       2966815019  Termination of pregnancy complicated by shock
609478003       2966818017  Termination of pregnancy complicated by perforation of vagina
609452007       2966819013  Termination of pregnancy complicated by renal failure
291591000119107 2959805014  Spontaneous subacute subdural haemorrhage
609480009       2966835018  Termination of pregnancy complicated by pulmonary embolism
609493004       2966837014  Termination of pregnancy complicated by tetanus
609451000       2966838016  Termination of pregnancy complicated by metabolic disorder
                    </pre>
                </div>
            </div>
        <h1><xsl:value-of select="@sn:name"/></h1>
        <xsl:apply-templates select="sn:file"/>
        <xsl:apply-templates select="sn:folder" />
        </body>
        <script type="text/javascript" src="//use.typekit.net/yny4pvk.js"></script>
        <script type="text/javascript">try{Typekit.load();}catch(e){}</script>
        </html>
    </xsl:template>
    
    
    <xsl:template match="sn:folder">
        <xsl:variable name="path">
            <xsl:for-each select="ancestor-or-self::*">
               <xsl:choose>
                    <xsl:when test="position() = 1"></xsl:when>
                    <xsl:when test="position() = 2"><xsl:value-of select="@sn:name"/></xsl:when>
                    <xsl:otherwise>/<xsl:value-of select="@sn:name"/></xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:variable>
        <div class="folder">
            <h2><a href="{$path}"><xsl:value-of select="@sn:name"/></a></h2>
            <xsl:for-each select = "sn:file">
                <xsl:apply-templates select="."/> 
            </xsl:for-each>            
            <xsl:for-each select = "sn:folder">
                <xsl:apply-templates select="."/> 
            </xsl:for-each>
        </div>
    </xsl:template>
    
    <xsl:template match="sn:file">
        <xsl:variable name="path">
            <xsl:for-each select="ancestor-or-self::*">
               <xsl:choose>
                    <xsl:when test="position() = 1"></xsl:when>
                    <xsl:when test="position() = 2"><xsl:value-of select="@sn:name"/></xsl:when>
                    <xsl:otherwise>/<xsl:value-of select="@sn:name"/></xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:variable>
        <div class="file clearfix">
            <div class="tooltip arrow">
              <a href="{$path}"><xsl:value-of select="@sn:name"/></a>
              <span class="popup">
<!--                   <div class="line"> -->
<!--                     <div class="title">Type</div> -->
<!--                     <div class="value"><xsl:value-of select="@sn:type"/></div> -->
<!--                   </div> -->
                  <div class="line">
                    <div class="title">Size</div>
                    <div class="value"><xsl:value-of select="format-number(@sn:size, '###,###,###')"/> bytes</div>
                  </div>
                  <div class="line">
                    <div class="title">Mimetype</div>
                    <div class="value"><xsl:value-of select="@sn:mimetype"/></div>
                  </div>            
              </span>  
            </div>
            <xsl:apply-templates select="sn:module"/>    
        </div>
    </xsl:template>
    
    <xsl:template match="sn:module">
        <div class="module">
            <h4>Contains module</h4>
            <h3><span class="name"><xsl:value-of select="@sn:name"/></span><span class="sid"><xsl:value-of select="@sn:sid"/></span></h3>
            <div class="refsets">
                <h4>Contains reference sets</h4>
                <ul>
                    <xsl:for-each select='sn:refset'>
                        <li><xsl:apply-templates select="."/></li>
                    </xsl:for-each>
                </ul>
            </div>
        </div>
    </xsl:template>
    
    <xsl:template match="sn:refset">
        <span class="name"><xsl:value-of select="@sn:name"/></span><span class="sid"><xsl:value-of select="@sn:sid"/></span>
    </xsl:template>    
    
</xsl:stylesheet>