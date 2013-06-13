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
            <link rel="stylesheet" href="manifest/screen.css?v=1.0"/>
            <script type="text/javascript">
            function changeOntology(value) {
                var redirect;
                redirect = "/ontology/" + value + "/concept/";
                document.location.href = redirect;
            }
            </script>
        </head>
        <body>
        <div class="intro" style="font-size: 0.8em; width: 60em">
            <p>The SNOMED CT International Release is provided in UTF-8 encoded tab-delimited flat files which can be imported into any database or other software application.  SNOMED CT is not software.</p>
            <p>The SNOMED CT files are designed as relational tables with each line in the file representing a row in the table. The first row of each table contains column headings. All other rows contain data.</p>
            <p>The SNOMED CT International Release is delivered to IHTSDO Member National Centers and authorized Affiliate Licensees via Internet download.</p>
            <p>© 2002-2012 The International Health Terminology Standards Development Organisation (IHTSDO).  All Rights Reserved.  SNOMED CT® was originally created by The College of American Pathologists.  "SNOMED" and "SNOMED CT" are registered trademarks of the IHTSDO.</p>
            <p>SNOMED CT has been created by combining SNOMED RT and a computer based nomenclature and classification known as Clinical Terms Version 3, formerly known as Read Codes Version 3, which was created on behalf of the UK Department of Health and is Crown copyright.</p>
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
            <h3><span class="name"><xsl:value-of select="@sn:name"/></span><span class="sid"><xsl:value-of select="@sn:sid"/></span></h3>
            <div class="refsets">
                <h4>Refsets</h4>
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