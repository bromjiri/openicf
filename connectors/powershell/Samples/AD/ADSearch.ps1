﻿# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
#
# Copyright (c) 2014 ForgeRock AS. All Rights Reserved
#
# The contents of this file are subject to the terms
# of the Common Development and Distribution License
# (the License). You may not use this file except in
# compliance with the License.
#
# You can obtain a copy of the License at
# http://forgerock.org/license/CDDLv1.0.html
# See the License for the specific language governing
# permission and limitations under the License.
#
# When distributing Covered Code, include this CDDL
# Header Notice in each file and include the License file
# at http://forgerock.org/license/CDDLv1.0.html
# If applicable, add the following below the CDDL Header,
# with the fields enclosed by brackets [] replaced by
# your own identifying information:
# " Portions Copyrighted [year] [name of copyright owner]"
#
# @author Gael Allioux <gael.allioux@forgerock.com>
#
#REQUIRES -Version 2.0
<#  
.SYNOPSIS  
    This is a sample Search script with Active Directory as a target
	
.DESCRIPTION

.INPUT VARIABLES
	The connector injects the following variables to the script:
	- <prefix>.Configuration : handler to the connector's configuration object
	- <prefix>.Options: a handler to the Operation Options
	- <prefix>.Operation: String correponding to the operation ("AUTHENTICATE" here)
	- <prefix>.ObjectClass: the Object class object (__ACCOUNT__ / __GROUP__ / other)	
	- <prefix>.Query: a handler to the Query.
	
.RETURNS
  
.NOTES  
    File Name      : ADSearch.ps1  
    Author         : Gael Allioux (gael.allioux@forgerock.com)
    Prerequisite   : PowerShell V2
    Copyright 2014 - ForgeRock AS    
.LINK  
    Script posted over:  
    http://openicf.forgerock.org
.EXAMPLE  
    Example 1     
.EXAMPLE    
    Example 2
#>

# We define a filter to process results through a pipe and feed the result handler
filter Process-Results {
	$result = @{"__UID__" = $_.ObjectGUID; "__NAME__" = $_.DistinguishedName}
			
	foreach($attrName in $Connector.Options.AttributesToGet)
	{
		if ($_.Contains($attrName))
		{
			if ($_.$attrName -eq $null)
			{
				$result.Add($attrName, $null)
			}
			elseif ($_.$attrName.GetType().Name -eq "ADPropertyValueCollection")
			{
				$values = @();
				foreach($val in $_.$attrName) 
				{
					$values += $val
				}
				$result.Add($attrName, $values)
			}
			else
			{
				$result.Add($attrName, $_.$attrName)
			}
		}
	}
	if (!$Connector.Result.Process($result))
	{
		break
	}
}

# Always put code in try/catch statement and make sure exceptions are re-thrown to connector
try
{
	$searchBase = 'ou=test,dc=example,dc=com'
	$attrsToGet = "*"
	$filter = "*"

	if ( $Connector.Query ) {$filter = $Connector.Query}

	switch ($Connector.ObjectClass.Type)
	{
		"__ACCOUNT__"
		{
			Get-ADUser -Filter $filter -SearchBase $searchBase -Properties $attrsToGet | Process-Results
		}
		"__GROUP__"
		{
			Get-ADGroup -Filter $filter -SearchBase $searchBase -Properties $attrsToGet | Process-Results
		}
	}	
}
catch #Re-throw the original exception
{
	throw
}