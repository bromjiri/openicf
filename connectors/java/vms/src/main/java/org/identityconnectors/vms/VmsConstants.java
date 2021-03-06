/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License").  You may not use this file
 * except in compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/cddl1.php
 * See the License for the specific language governing permissions and limitations
 * under the License.
 *
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://opensource.org/licenses/cddl1.php.
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package org.identityconnectors.vms;

public interface VmsConstants {
    /* @formatter:off */
    // Names of ATTRIBUTES
    //
    public static final String ATTR_ACCOUNT             = "ACCOUNT";
    public static final String ATTR_ALGORITHM           = "ALGORITHM";
    public static final String ATTR_ASTLM               = "ASTLM";
    public static final String ATTR_BATCH               = "BATCH";
    public static final String ATTR_BIOLM               = "BIOLM";
    public static final String ATTR_BYTLM               = "BYTLM";
    public static final String ATTR_CLI                 = "CLI";
    public static final String ATTR_CLITABLES           = "CLITABLES";
    public static final String ATTR_CPUTIME             = "CPUTIME";
    public static final String ATTR_DEFPRIVILEGES       = "DEFPRIVILEGES";
    public static final String ATTR_DEFAULT             = "DEFAULT";
    public static final String ATTR_DEVICE              = "DEVICE";
    public static final String ATTR_DIALUP              = "DIALUP";
    public static final String ATTR_DIOLM               = "DIOLM";
    public static final String ATTR_DIRECTORY           = "DIRECTORY";
    public static final String ATTR_ENQLM               = "ENQLM";
    public static final String ATTR_EXPIRATION          = "EXPIRATION";
    public static final String ATTR_FILLM               = "FILLM";
    public static final String ATTR_FLAGS               = "FLAGS";
    public static final String ATTR_GRANT_IDS           = "GRANT_IDS";
    public static final String ATTR_GENERATE_PASSWORD   = "GENERATE_PASSWORD";
    public static final String ATTR_INTERACTIVE         = "INTERACTIVE";
    public static final String ATTR_JTQUOTA             = "JTQUOTA";
    public static final String ATTR_LGICMD              = "LGICMD";
    public static final String ATTR_LOCAL               = "LOCAL";
    public static final String ATTR_LOGIN_FAILS         = "LoginFails";
    public static final String ATTR_MAXACCTJOBS         = "MAXACCTJOBS";
    public static final String ATTR_MAXDETACH           = "MAXDETACH";
    public static final String ATTR_MAXJOBS             = "MAXJOBS";
    public static final String ATTR_NETWORK             = "NETWORK";
    public static final String ATTR_OWNER               = "OWNER";
    public static final String ATTR_PBYTLM              = "PBYTLM";
    public static final String ATTR_PGFLQUOTA           = "PGFLQUOTA";
    public static final String ATTR_PRCLM               = "PRCLM";
    public static final String ATTR_PRIMEDAYS           = "PRIMEDAYS";
    public static final String ATTR_PRIORITY            = "PRIORITY";
    public static final String ATTR_PRIVILEGES          = "PRIVILEGES";
    public static final String ATTR_PWDEXPIRED          = "PWDEXPIRED";
    public static final String ATTR_PWDMINIMUM          = "PWDMINIMUM";
    public static final String ATTR_QUEPRIO             = "QUEPRIO";
    public static final String ATTR_REMOTE              = "REMOTE";
    public static final String ATTR_SECDAYS             = "Secondary Days";
    public static final String ATTR_SHRFILLM            = "SHRFILLM";
    public static final String ATTR_TQELM               = "TQELM";
    public static final String ATTR_UIC                 = "UIC";
    public static final String ATTR_WSDEFAULT           = "WSDEFAULT";
    public static final String ATTR_WSEXTENT            = "WSEXTENT";
    public static final String ATTR_WSQUOTA             = "WSQUOTA";

    public static final String ATTR_COPY_LOGIN_SCRIPT   = "COPY_LOGIN_SCRIPT";
    public static final String ATTR_CREATE_DIRECTORY    = "CREATE_DIRECTORY";
    public static final String ATTR_LOGIN_SCRIPT_SOURCE = "LOGIN_SCRIPT_SOURCE";

    // VMS Flags
    //
    public static final String FLAG_AUDIT               = "AUDIT";
    public static final String FLAG_AUTOLOGIN           = "AUTOLOGIN";
    public static final String FLAG_CAPTIVE             = "CAPTIVE";
    public static final String FLAG_DEFCLI              = "DEFCLI";
    public static final String FLAG_DISCTLY             = "DISCTLY";
    public static final String FLAG_DISFORCE_PWD_CHANGE = "DISFORCE_PWD_CHANGE";
    public static final String FLAG_DISIMAGE            = "DISIMAGE";
    public static final String FLAG_DISMAIL             = "DISMAIL";
    public static final String FLAG_DISNEWMAIL          = "DISNEWMAIL";
    public static final String FLAG_DISPWDDIC           = "DISPWDDIC";
    public static final String FLAG_DISPWDHIS           = "DISPWDHIS";
    public static final String FLAG_DISPWDSYNCH         = "DISPWDSYNCH";
    public static final String FLAG_DISRECONNECT        = "DISRECONNECT";
    public static final String FLAG_DISREPORT           = "DISREPORT";
    public static final String FLAG_DISUSER             = "DISUSER";
    public static final String FLAG_DISWELCOME          = "DISWELCOME";
    public static final String FLAG_EXTAUTH             = "EXTAUTH";
    public static final String FLAG_GENPWD              = "GENPWD";
    public static final String FLAG_LOCKPWD             = "LOCKPWD";
    public static final String FLAG_PWD_EXPIRED         = "PWD_EXPIRED";
    public static final String FLAG_PWD2_EXPIRED        = "PWD2_EXPIRED";
    public static final String FLAG_PWDMIX              = "PWDMIX";
    public static final String FLAG_RESTRICTED          = "RESTRICTED";
    public static final String FLAG_VMSAUTH             = "VMSAUTH";

    // VMS Privileges
    //
    public static final String PRIV_ACNT                = "ACNT";
    public static final String PRIV_ALLSPOOL            = "ALLSPOOL";
    public static final String PRIV_ALTPRI              = "ALTPRI";
    public static final String PRIV_AUDIT               = "AUDIT";
    public static final String PRIV_BUGCHK              = "BUGCHK";
    public static final String PRIV_BYPASS              = "BYPASS";
    public static final String PRIV_CMEXEC              = "CMEXEC";
    public static final String PRIV_CMKRNL              = "CMKRNL";
    public static final String PRIV_DIAGNOSE            = "DIAGNOSE";
    public static final String PRIV_DOWNGRADE           = "DOWNGRADE";
    public static final String PRIV_EXQUOTA             = "EXQUOTA";
    public static final String PRIV_GROUP               = "GROUP";
    public static final String PRIV_GRPNAM              = "GRPNAM";
    public static final String PRIV_GRPPRV              = "GRPPRV";
    public static final String PRIV_IMPERSONATE         = "IMPERSONATE";
    public static final String PRIV_IMPORT              = "IMPORT";
    public static final String PRIV_LOG_IO              = "LOG_IO";
    public static final String PRIV_MOUNT               = "MOUNT";
    public static final String PRIV_NETMBX              = "NETMBX";
    public static final String PRIV_OPER                = "OPER";
    public static final String PRIV_PFNMAP              = "PFNMAP";
    public static final String PRIV_PHY_IO              = "PHY_IO";
    public static final String PRIV_PRMCEB              = "PRMCEB";
    public static final String PRIV_PRMGBL              = "PRMGBL";
    public static final String PRIV_PRMMBX              = "PRMMBX";
    public static final String PRIV_PSWAPM              = "PSWAPM";
    public static final String PRIV_READALL             = "READALL";
    public static final String PRIV_SECURITY            = "SECURITY";
    public static final String PRIV_SETPRV              = "SETPRV";
    public static final String PRIV_SHARE               = "SHARE";
    public static final String PRIV_SHMEM               = "SHMEM";
    public static final String PRIV_SYSGBL              = "SYSGBL";
    public static final String PRIV_SYSLCK              = "SYSLCK";
    public static final String PRIV_SYSNAM              = "SYSNAM";
    public static final String PRIV_SYSPRV              = "SYSPRV";
    public static final String PRIV_TMPMBX              = "TMPMBX";
    public static final String PRIV_UPGRADE             = "UPGRADE";
    public static final String PRIV_VOLPRO              = "VOLPRO";
    public static final String PRIV_WORLD               = "WORLD";

    // Access
    //
    public static final String ACCESS_PRIMARY           = "PRIMARY";
    public static final String ACCESS_SECONDARY         = "SECONDARY";

    // generate password
    //
    public static final String PWD_TYPE_BOTH             = "BOTH";
    public static final String PWD_TYPE_CURRENT          = "CURRENT";
    public static final String PWD_TYPE_PRIMARY          = "PRIMARY";
    public static final String PWD_TYPE_SECONDARY        = "SECONDARY";

    // Algorithm
    //
    public static final String ALGO_KEY_BOTH             = "BOTH";
    public static final String ALGO_KEY_CURRENT          = "CURRENT";
    public static final String ALGO_KEY_PRIMARY          = "PRIMARY";
    public static final String ALGO_KEY_SECONDARY        = "SECONDARY";

    public static final String ALGO_TYPE_VMS             = "VMS";
    public static final String ALGO_TYPE_CUSTOMER        = "CUSTOMER";

    // Days of the week
    public static final String DAYS_SUN                  = "Sun";
    public static final String DAYS_MON                  = "Mon";
    public static final String DAYS_TUE                  = "Tue";
    public static final String DAYS_WED                  = "Wed";
    public static final String DAYS_THU                  = "Thu";
    public static final String DAYS_FRI                  = "Fri";
    public static final String DAYS_SAT                  = "Sat";
    /* @formatter:on */
}
