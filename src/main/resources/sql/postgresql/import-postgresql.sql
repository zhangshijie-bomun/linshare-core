-- ###BEGIN-PART-1###
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
\set ON_ERROR_STOP

BEGIN;
-- default domain policy
INSERT INTO domain_access_policy(id) VALUES (1);
INSERT INTO domain_access_rule(id, domain_access_rule_type, ls_regexp, domain_id, domain_access_policy_id, rule_index) VALUES (1, 0, '', null, 1,0);
INSERT INTO domain_policy(id, uuid, label, domain_access_policy_id) VALUES (1, 'DefaultDomainPolicy', 'DefaultDomainPolicy', 1);


-- Root domain (application domain)
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, default_mail_locale, used_space, user_provider_id, domain_policy_id, parent_id,
	auth_show_order) VALUES (1, 0, 'LinShareRootDomain', 'LinShareRootDomain', true, false, 'The root application domain', 3, 'en', 'en', 0, null, 1, null, 0);

-- root domain quota
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	current_value, last_value, domain_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    quota_type, current_value_for_subdomains)
VALUES (1, '2a01ac66-a279-11e5-9086-5404a683a462', NOW(), NOW(), NOW(),
	0, 0, 1,
	10995116277760, null,
	10995116277760,
    1099511627776, true,
    'DOMAIN_QUOTA', 0);
-- quota : 10 To
-- quota_warning : 10995116277760 : 10 To
-- default_quota : 1099511627776 : 1 To (1 To per sub domain)


-- 'CONTAINER_QUOTA', 'USER' for root domain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, current_value, last_value, domain_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    default_max_file_size, default_max_file_size_override,
    default_account_quota, default_account_quota_override,
    quota_type, container_type, shared)
VALUES (11, '26323798-a1a8-11e6-ad47-0800271467bb', NOW(), NOW(), NOW(),
	1, 0, 0, 1,
	429496729600, null,
    429496729600,
    429496729600, false,
    10737418240, null,
    107374182400, null,
    'CONTAINER_QUOTA', 'USER', false);
-- quota : 429496729600 : 400 Go for all users
-- quota_warning : 429496729600 : 400 Go
-- default_quota : 429496729600 : 400 Go
-- default_max_file_size : 10737418240  : 10 Go
-- default_account_quota : 107374182400 : 100 Go


-- 'CONTAINER_QUOTA', 'WORK_GROUP' for root domain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, current_value, last_value, domain_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    default_max_file_size, default_max_file_size_override,
    default_account_quota, default_account_quota_override,
    quota_type, container_type, shared)
VALUES (12, '63de4f14-a1a8-11e6-a369-0800271467bb', NOW(), NOW(), NOW(),
	1, 0, 0, 1,
	429496729600, null,
    429496729600,
    429496729600, false,
    10737418240, null,
    429496729600, null,
    'CONTAINER_QUOTA', 'WORK_GROUP', true);
-- quota : 429496729600 : 400 Go for all workgroups
-- quota_warning : 429496729600 : 400 Go
-- default_quota : 429496729600 : 400 Go
-- default_max_file_size : 10737418240  : 10 Go
-- default_account_quota : 429496729600 : 400 Go, also 400 Go for one workgroup



-- Default mime policy
INSERT INTO mime_policy(id, domain_id, uuid, name, mode, displayable, version, creation_date, modification_date) VALUES(1, 1, '3d6d8800-e0f7-11e3-8ec0-080027c0eef0', 'Default Mime Policy', 0, 0, 1, now(), now());
UPDATE domain_abstract SET mime_policy_id=1;


--Welcome messages
INSERT INTO welcome_messages(id, uuid, name, description, creation_date, modification_date, domain_id) VALUES (1, '4bc57114-c8c9-11e4-a859-37b5db95d856', 'WelcomeName', 'a Welcome description', now(), now(), 1);

--Melcome messages Entry
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (1, 'en', 'Welcome to LinShare, THE Secure, Open-Source File Sharing Tool.', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (2, 'fr', 'Bienvenue dans LinShare, le logiciel libre de partage de fichiers sécurisé.', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (3, 'mq', 'Bienvini an lè Linshare, an solusyon lib de partaj de fichié sékirisé.', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (4, 'vi', 'Chào mừng bạn đến với Linshare, phần mềm nguồn mở chia sẻ file bảo mật.', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (5, 'nl', 'Welkom bij LinShare, het Open Source-systeem om grote bestanden te delen.', 1);

-- Default setting welcome messages for all domains
UPDATE domain_abstract SET welcome_messages_id = 1;


-- system
-- OBM user ldap pattern.
INSERT INTO ldap_pattern(
    id,
    uuid,
    pattern_type,
    label,
    description,
    auth_command,
    search_user_command,
    system,
    auto_complete_command_on_first_and_last_name,
    auto_complete_command_on_all_attributes,
    search_page_size,
    search_size_limit,
    completion_page_size,
    completion_size_limit,
    creation_date,
    modification_date)
VALUES (
    1,
    'cd26e59d-6d4c-41b4-a0eb-610fd42e1beb',
    'USER_LDAP_PATTERN',
    'default-pattern-obm',
    'This is pattern the default pattern for the ldap obm structure.',
    'ldap.search(domain, "(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");',
    'ldap.search(domain, "(&(objectClass=obmUser)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
    true,
    'ldap.search(domain, "(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
    'ldap.search(domain, "(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
    100,
    100,
    10,
    10,
    now(),
    now()
);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (1, 'user_mail', 'mail', false, true, true, 1, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (2, 'user_firstname', 'givenName', false, true, true, 1, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (3, 'user_lastname', 'sn', false, true, true, 1, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (4, 'user_uid', 'uid', false, true, true, 1, false);

-- Active Directory domain pattern.
INSERT INTO ldap_pattern(
    id,
    uuid,
    pattern_type,
    label,
    description,
    auth_command,
    search_user_command,
    system,
    auto_complete_command_on_first_and_last_name,
    auto_complete_command_on_all_attributes,
    search_page_size,
    search_size_limit,
    completion_page_size,
    completion_size_limit,
    creation_date,
    modification_date)
VALUES (
    2,
    'af7ceb1e-9268-4b20-af80-21fa4bd5222c',
    'USER_LDAP_PATTERN',
    'default-pattern-AD',
    'This is pattern the default pattern for the Active Directory structure.',
    'ldap.search(domain, "(&(objectClass=user)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(sAMAccountName="+login+")))");',
    'ldap.search(domain, "(&(objectClass=user)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
    true,
    'ldap.search(domain, "(&(objectClass=user)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
    'ldap.search(domain, "(&(objectClass=user)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
    100,
    100,
    10,
    10,
    now(),
    now()
);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (5, 'user_mail', 'mail', false, true, true, 2, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (6, 'user_firstname', 'givenName', false, true, true, 2, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (7, 'user_lastname', 'sn', false, true, true, 2, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (8, 'user_uid', 'sAMAccountName', false, true, true, 2, false);

-- OpenLdap ldap pattern.
INSERT INTO ldap_pattern(
    id,
    uuid,
    pattern_type,
    label,
    description,
    auth_command,
    search_user_command,
    system,
    auto_complete_command_on_first_and_last_name,
    auto_complete_command_on_all_attributes,
    search_page_size,
    search_size_limit,
    completion_page_size,
    completion_size_limit,
    creation_date,
    modification_date)
VALUES (
    3,
    '868400c0-c12e-456a-8c3c-19e985290586',
    'USER_LDAP_PATTERN',
    'default-pattern-openldap',
    'This is pattern the default pattern for the OpenLdap structure.',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
    true,
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
    100,
    100,
    10,
    10,
    now(),
    now()
);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (9, 'user_mail', 'mail', false, true, true, 3, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (10, 'user_firstname', 'givenName', false, true, true, 3, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (11, 'user_lastname', 'sn', false, true, true, 3, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (12, 'user_uid', 'uid', false, true, true, 3, false);


-- login is e-mail address 'root@localhost.localdomain' and password is 'adminlinshare'
INSERT INTO account(id, Mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale,cmis_locale, enable, password, destroyed, domain_id) VALUES (1, 'root@localhost.localdomain', 6, 'root@localhost.localdomain', now(),now(), 3, 'en', 'en','en', true, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', 0, 1);
INSERT INTO users(account_id, First_name, Last_name, Can_upload, Comment, Restricted, CAN_CREATE_GUEST) VALUES (1, 'Administrator', 'LinShare', false, '', false, false);

-- system account :
INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale,cmis_locale, enable, destroyed, domain_id) VALUES (2, 'system', 7, 'system', now(),now(), 3, 'en', 'en','en', true, 0, 1);
-- system account for upload-request:
INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale,cmis_locale, enable, destroyed, domain_id) VALUES (3,'system-account-uploadrequest', 7, 'system-account-uploadrequest', now(),now(), 3, 'en', 'en','en', true, 0, 1);

-- system account for upload-proposition
INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale,cmis_locale, enable, password, destroyed, domain_id)
	VALUES (4,'linshare-noreply@linagora.com', 4, '89877610-574a-4e79-aeef-5606b96bde35', now(),now(), 5, 'en', 'en','en', true, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', 0, 1);
INSERT INTO users(account_id, first_name, last_name, can_upload, comment, restricted, can_create_guest)
	VALUES (4, null, 'Technical Account for upload proposition', false, '', false, false);


-- unit type : TIME(0), SIZE(1)
-- unit value : FileSizeUnit : KILO(0), MEGA(1), GIGA(2)
-- unit value : TimeUnit : DAY(0), WEEK(1), MONTH(2)
-- Policies : MANDATORY(0), ALLOWED(1), FORBIDDEN(2)



-- Functionality : BEGIN
-- Functionality : FILESIZE_MAX
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (1, true, true, 1, false);
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (2, true, true, 1, false);
-- if a functionality is system, you will not be able see/modify its parameters
-- INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (1, false, 'FILESIZE_MAX', 1, 2, 1);
-- INSERT INTO unit(id, unit_type, unit_value) VALUES (1, 1, 1);
-- INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (1, 10, 1);


-- Functionality : QUOTA_GLOBAL
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (3, false, false, 1, false);
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (4, true, true, 1, false);
-- INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (2, false, 'QUOTA_GLOBAL', 3, 4, 1);
-- INSERT INTO unit(id, unit_type, unit_value) VALUES (2, 1, 1);
-- INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (2, 1, 2);


-- Functionality : QUOTA_USER
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (5, true, true, 1, false);
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (6, true, true, 1, false);
-- INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (3, false, 'QUOTA_USER', 5, 6, 1);
-- INSERT INTO unit(id, unit_type, unit_value) VALUES (3, 1, 1);
-- INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (3, 100, 3);


-- Functionality : MIME_TYPE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (7, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (8, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (4, true, 'MIME_TYPE', 7, 8, 1);


--This functionality is not yet available in LinShare 2.0.0
---- Functionality : SIGNATURE
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (9, false, false, 1, false);
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (10, false, false, 2, true);
--INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (5, true, 'SIGNATURE', 9, 10, 1);

--This functionality is not yet available in LinShare 2.0.0
---- Functionality : ENCIPHERMENT
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (11, false, false, 1, false);
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (12, false, false, 2, true);
--INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (6, true, 'ENCIPHERMENT', 11, 12, 1);


-- Functionality : TIME_STAMPING
INSERT INTO policy(id, status, default_status, policy, system) VALUES (13, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (14, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (7, false, 'TIME_STAMPING', 13, 14, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (7, 'http://localhost:8080/signserver/tsa?signerId=1');


-- Functionality : ANTIVIRUS
INSERT INTO policy(id, status, default_status, policy, system) VALUES (15, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (16, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (8, true, 'ANTIVIRUS', 15, 16, 1);

--useless - deleted
---- Functionality : CUSTOM_LOGO
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (17, false, false, 1, false);
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (18, true, true, 1, false);
--INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (9, false, 'CUSTOM_LOGO', 17, 18, 1);
--INSERT INTO functionality_string(functionality_id, string_value) VALUES (9, 'http://linshare-ui-user.local/custom/images/logo.png');

--useless - deleted
---- Functionality : CUSTOM_LOGO__LINK
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (59, false, false, 1, false);
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (60, false, false, 1, false);
--INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param) VALUES (29, false, 'CUSTOM_LOGO__LINK', 59, 60, 1, 'CUSTOM_LOGO', true);
--INSERT INTO functionality_string(functionality_id, string_value) VALUES (29, 'http://linshare-ui-user.local');


-- Functionality : GUESTS
INSERT INTO policy(id, status, default_status, policy, system) VALUES (27, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (28, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (14, true, 'GUESTS', 27, 28, 1);

-- Functionality : GUESTS__EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (19, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (20, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (111, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param) VALUES (10, false, 'GUESTS__EXPIRATION', 19, 20, 111, 1, 'GUESTS', true);
INSERT INTO unit(id, unit_type, unit_value) VALUES (4, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (10, 3, 4);

-- Functionality : GUESTS__RESTRICTED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (47, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (48, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (112, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param) VALUES (24, false, 'GUESTS__RESTRICTED', 47, 48, 112, 1, 'GUESTS', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (24, true);

-- Functionality : GUESTS__CAN_UPLOAD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (113, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (114, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (115, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param) VALUES (48, false, 'GUESTS__CAN_UPLOAD', 113, 114, 115, 1, 'GUESTS', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (48, true);

-- Functionality : FILE_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (21, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (22, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (11, false, 'FILE_EXPIRATION', 21, 22, 1);
INSERT INTO unit(id, unit_type, unit_value) VALUES (5, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (11, 3, 5);


-- Functionality : SHARE_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (23, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (24, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (122, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id) VALUES (12, false, 'SHARE_EXPIRATION', 23, 24, 122, 1);
INSERT INTO unit(id, unit_type, unit_value) VALUES (6, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (12, 3, 6);

-- Functionality : SHARE_EXPIRATION__DELETE_FILE_ON_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (120, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (121, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param) VALUES (50, false, 'SHARE_EXPIRATION__DELETE_FILE_ON_EXPIRATION', 120, 121, 1, 'SHARE_EXPIRATION', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (50, false);

-- Functionality : ANONYMOUS_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (25, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (26, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (116, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id) VALUES (13, false, 'ANONYMOUS_URL', 25, 26, 116, 1);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (13, true);


-- Functionality : INTERNAL_CAN_UPLOAD formerly known as USER_CAN_UPLOAD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (29, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (30, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (15, true, 'INTERNAL_CAN_UPLOAD', 29, 30, 1);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (15, true);


-- Functionality : COMPLETION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (31, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (32, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (16, false, 'COMPLETION', 31, 32, 1);
INSERT INTO functionality_integer(functionality_id, integer_value) VALUES (16, 3);

--useless - deleted
---- Functionality : TAB_HELP
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (33, true, true, 1, false);
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (34, false, false, 1, true);
--INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (17, true, 'TAB_HELP', 33, 34, 1);

--useless - deleted
---- Functionality : TAB_AUDIT
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (35, true, true, 1, false);
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (36, false, false, 1, true);
--INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (18, true, 'TAB_AUDIT', 35, 36, 1);

--useless - deleted
---- Functionality : TAB_USER
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (37, true, true, 1, false);
--INSERT INTO policy(id, status, default_status, policy, system) VALUES (38, false, false, 1, true);
--INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (19, true, 'TAB_USER', 37, 38, 1);

-- Functionality : SHARE_NOTIFICATION_BEFORE_EXPIRATION
-- Policies : MANDATORY(0), ALLOWED(1), FORBIDDEN(2)
INSERT INTO policy(id, status, default_status, policy, system) VALUES (43, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (44, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (22, false, 'SHARE_NOTIFICATION_BEFORE_EXPIRATION', 43, 44, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (22, '2,7');

-- Functionality : WORK_GROUP
INSERT INTO policy(id, status, default_status, policy, system) VALUES (45, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (46, false, false, 1, true);
-- if a functionality is system, you will not be able see/modify its parameters
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (23, true, 'WORK_GROUP', 45, 46, 1);

-- Functionality : WORK_GROUP__CREATION_RIGHT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (57, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (58, false, false, 1, false);
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (117, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param) VALUES (28, false, 'WORK_GROUP__CREATION_RIGHT', 57, 58, 1, 'WORK_GROUP', true);
-- INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (28, true);

-- Functionality : CONTACTS_LIST
INSERT INTO policy(id, status, default_status, policy, system) VALUES (53, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (54, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (26, true, 'CONTACTS_LIST', 53, 54, 1);

--Functionality : CONTACTS_LIST__CREATION_RIGHT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (55, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (56, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (227, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param) VALUES(27, false, 'CONTACTS_LIST__CREATION_RIGHT', 55, 56, 227, 1, 'CONTACTS_LIST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (27, false);

-- Functionality : DOMAIN
INSERT INTO policy(id, status, default_status, policy, system) VALUES (118, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (119, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES(49, false, 'DOMAIN', 118, 119, 1);

-- Functionality : DOMAIN__NOTIFICATION_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (61, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (62, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param) VALUES(30, false, 'DOMAIN__NOTIFICATION_URL', 61, 62, 1, 'DOMAIN', true);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (30, 'http://linshare-ui-user.local/');

-- Functionality : DOMAIN__MAIL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (49, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (50, false, false, 2, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param) VALUES (25, false, 'DOMAIN__MAIL', 49, 50, 1, 'DOMAIN', true);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (25, 'linshare-noreply@linagora.com');


-- Functionality : UPLOAD_REQUEST
INSERT INTO policy(id, status, default_status, policy, system) VALUES (63, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (64, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES(31, false, 'UPLOAD_REQUEST', 63, 64, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (31, 'http://linshare-upload-request.local');

-- Functionality : UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (65, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (66, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (67, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(32, false, 'UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION', 65, 66, 67, 1, 'UPLOAD_REQUEST', true);
INSERT INTO unit(id, unit_type, unit_value) VALUES (7, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (32, 0, 7);

-- Functionality : UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (68, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (69, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (70, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(33, false, 'UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION', 68, 69, 70, 1, 'UPLOAD_REQUEST', true);
-- time unit : month
 INSERT INTO unit(id, unit_type, unit_value) VALUES (8, 0, 2);
-- month : 1 month
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (33, 1, 8);

-- Functionality : UPLOAD_REQUEST__GROUPED_MODE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (71, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (72, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (73, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(34, false, 'UPLOAD_REQUEST__GROUPED_MODE', 71, 72, 73, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (34, false);

-- Functionality : UPLOAD_REQUEST__MAXIMUM_FILE_COUNT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (74, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (75, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (76, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(35, false, 'UPLOAD_REQUEST__MAXIMUM_FILE_COUNT', 74, 75, 76, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_integer(functionality_id, integer_value) VALUES (35, 3);

-- Functionality : UPLOAD_REQUEST__MAXIMUM_FILE_SIZE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (77, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (78, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (79, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(36, false, 'UPLOAD_REQUEST__MAXIMUM_FILE_SIZE', 77, 78, 79, 1, 'UPLOAD_REQUEST', true);
 -- file size unit : Mega
INSERT INTO unit(id, unit_type, unit_value) VALUES (9, 1, 1);
-- size : 10 Mega
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (36, 10, 9);

-- Functionality : UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (80, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (81, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (82, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(37, false, 'UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE', 80, 81, 82, 1, 'UPLOAD_REQUEST', true);
 -- file size unit : Mega
INSERT INTO unit(id, unit_type, unit_value) VALUES (10, 1, 1);
-- size : 30 Mega
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (37, 30, 10);

-- Functionality : UPLOAD_REQUEST__NOTIFICATION_LANGUAGE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (83, true, true, 1, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (84, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (85, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(38, false, 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE', 83, 84, 85, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_enum_lang(functionality_id, lang_value) VALUES (38, 'en');

-- Functionality : UPLOAD_REQUEST__SECURED_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (86, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (87, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (88, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(39, false, 'UPLOAD_REQUEST__SECURED_URL', 86, 87, 88, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (39, false);

-- Functionality : UPLOAD_REQUEST__PROLONGATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (89, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (90, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (91, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(40, false, 'UPLOAD_REQUEST__PROLONGATION', 89, 90, 91, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (40, false);

-- Functionality : UPLOAD_REQUEST__CAN_DELETE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (92, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (93, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (94, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(41, false, 'UPLOAD_REQUEST__CAN_DELETE', 92, 93, 94, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (41, true);

-- Functionality : UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (95, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (96, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (97, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(42, false, 'UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION', 95, 96, 97, 1, 'UPLOAD_REQUEST', true);
-- time unit : day
INSERT INTO unit(id, unit_type, unit_value) VALUES (11, 0, 0);
-- time : 7 days
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (42, 7, 11);

-- Functionality : UPLOAD_REQUEST__CAN_CLOSE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (98, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (99, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (100, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(43, false, 'UPLOAD_REQUEST__CAN_CLOSE', 98, 99, 100, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (43, true);

 -- Functionality : UPLOAD_PROPOSITION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (101, false, false, 2, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (102, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id)
 VALUES(44, false, 'UPLOAD_PROPOSITION', 101, 102, 1);

-- Functionality : GUEST__EXPIRATION_ALLOW_PROLONGATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (123, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (124, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (125, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param) VALUES(51, false, 'GUESTS__EXPIRATION_ALLOW_PROLONGATION', 123, 124, 125, 1, 'GUESTS', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (51, true);

-- Functionality : UPLOAD_REQUEST_ENABLE_TEMPLATE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (129, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (130, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, param)
 VALUES(53, false, 'UPLOAD_REQUEST_ENABLE_TEMPLATE', 129, 130, 1, false);

-- Functionality : SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (126, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (127, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (128, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id) VALUES(52, false, 'SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER', 126, 127, 128, 1);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (52, true);

-- Functionality : UNDOWNLOADED_SHARED_DOCUMENTS_ALERT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (131, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (132, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (133, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id)
 VALUES(54, false, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT', 131, 132, 133, 1);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (54, true);

-- Functionality : UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (134, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (135, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (136, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(55, false, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION', 134, 135, 136, 1, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT', true);
INSERT INTO functionality_integer(functionality_id, integer_value) VALUES (55, 3);

-- Functionality : ANONYMOUS_URL__NOTIFICATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (224, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (225, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (226, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(56, false, 'ANONYMOUS_URL__NOTIFICATION', 224, 225, 226, 1, 'ANONYMOUS_URL', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (56, true);

-- Functionality : ANONYMOUS_URL__NOTIFICATION_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (228, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (229, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (230, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 	VALUES(57, false, 'ANONYMOUS_URL__NOTIFICATION_URL', 228, 229, 230, 1, 'ANONYMOUS_URL', true);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (57, 'http://linshare-ui-user.local/');

-- Functionality : END


-- MailActivation : BEGIN

-- MailActivation : ANONYMOUS_DOWNLOAD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (137, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (138, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (139, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(1, false, 'ANONYMOUS_DOWNLOAD', 137, 138, 139, 1, true);

-- MailActivation : REGISTERED_DOWNLOAD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (140, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (141, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (142, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(2, false, 'REGISTERED_DOWNLOAD', 140, 141, 142, 1, true);

-- MailActivation : NEW_GUEST
INSERT INTO policy(id, status, default_status, policy, system) VALUES (143, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (144, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (145, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(3, false, 'NEW_GUEST', 143, 144, 145, 1, true);

-- MailActivation : RESET_PASSWORD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (146, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (147, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (148, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(4, false, 'RESET_PASSWORD', 146, 147, 148, 1, true);

-- MailActivation : SHARED_DOC_UPDATED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (149, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (150, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (151, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(5, false, 'SHARED_DOC_UPDATED', 149, 150, 151, 1, true);

-- MailActivation : SHARED_DOC_DELETED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (152, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (153, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (154, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(6, false, 'SHARED_DOC_DELETED', 152, 153, 154, 1, true);

-- MailActivation : SHARED_DOC_UPCOMING_OUTDATED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (155, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (156, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (157, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(7, false, 'SHARED_DOC_UPCOMING_OUTDATED', 155, 156, 157, 1, true);

-- MailActivation : DOC_UPCOMING_OUTDATED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (158, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (159, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (160, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(8, false, 'DOC_UPCOMING_OUTDATED', 158, 159, 160, 1, true);

-- MailActivation : NEW_SHARING
INSERT INTO policy(id, status, default_status, policy, system) VALUES (161, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (162, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (163, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(9, false, 'NEW_SHARING', 161, 162, 163, 1, true);

-- MailActivation : UPLOAD_PROPOSITION_CREATED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (164, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (165, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (166, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(10, false, 'UPLOAD_PROPOSITION_CREATED', 164, 165, 166, 1, true);

-- MailActivation : UPLOAD_PROPOSITION_REJECTED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (167, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (168, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (169, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(11, false, 'UPLOAD_PROPOSITION_REJECTED', 167, 168, 169, 1, true);

-- MailActivation : UPLOAD_REQUEST_UPDATED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (170, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (171, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (172, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(12, false, 'UPLOAD_REQUEST_UPDATED', 170, 171, 172, 1, true);

-- MailActivation : UPLOAD_REQUEST_ACTIVATED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (173, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (174, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (175, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(13, false, 'UPLOAD_REQUEST_ACTIVATED', 173, 174, 175, 1, true);

-- MailActivation : UPLOAD_REQUEST_AUTO_FILTER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (176, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (177, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (178, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(14, false, 'UPLOAD_REQUEST_AUTO_FILTER', 176, 177, 178, 1, true);

-- MailActivation : UPLOAD_REQUEST_CREATED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (179, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (180, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (181, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(15, false, 'UPLOAD_REQUEST_CREATED', 179, 180, 181, 1, true);

-- MailActivation : UPLOAD_REQUEST_ACKNOWLEDGEMENT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (182, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (183, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (184, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(16, false, 'UPLOAD_REQUEST_ACKNOWLEDGEMENT', 182, 183, 184, 1, true);

-- MailActivation : UPLOAD_REQUEST_REMINDER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (185, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (186, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (187, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(17, false, 'UPLOAD_REQUEST_REMINDER', 185, 186, 187, 1, true);

-- MailActivation : UPLOAD_REQUEST_WARN_OWNER_BEFORE_EXPIRY
INSERT INTO policy(id, status, default_status, policy, system) VALUES (188, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (189, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (190, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(18, false, 'UPLOAD_REQUEST_WARN_OWNER_BEFORE_EXPIRY', 188, 189, 190, 1, true);

-- MailActivation : UPLOAD_REQUEST_WARN_RECIPIENT_BEFORE_EXPIRY
INSERT INTO policy(id, status, default_status, policy, system) VALUES (191, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (192, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (193, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(19, false, 'UPLOAD_REQUEST_WARN_RECIPIENT_BEFORE_EXPIRY', 191, 192, 193, 1, true);

-- MailActivation : UPLOAD_REQUEST_WARN_OWNER_EXPIRY
INSERT INTO policy(id, status, default_status, policy, system) VALUES (194, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (195, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (196, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(20, false, 'UPLOAD_REQUEST_WARN_OWNER_EXPIRY', 194, 195, 196, 1, true);

-- MailActivation : UPLOAD_REQUEST_WARN_RECIPIENT_EXPIRY
INSERT INTO policy(id, status, default_status, policy, system) VALUES (197, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (198, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (199, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(21, false, 'UPLOAD_REQUEST_WARN_RECIPIENT_EXPIRY', 197, 198, 199, 1, true);

-- MailActivation : UPLOAD_REQUEST_CLOSED_BY_RECIPIENT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (200, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (201, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (202, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(22, false, 'UPLOAD_REQUEST_CLOSED_BY_RECIPIENT', 200, 201, 202, 1, true);

-- MailActivation : UPLOAD_REQUEST_CLOSED_BY_OWNER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (203, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (204, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (205, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(23, false, 'UPLOAD_REQUEST_CLOSED_BY_OWNER', 203, 204, 205, 1, true);

-- MailActivation : UPLOAD_REQUEST_DELETED_BY_OWNER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (206, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (207, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (208, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(24, false, 'UPLOAD_REQUEST_DELETED_BY_OWNER', 206, 207, 208, 1, true);

-- MailActivation : UPLOAD_REQUEST_NO_SPACE_LEFT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (209, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (210, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (211, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(25, false, 'UPLOAD_REQUEST_NO_SPACE_LEFT', 209, 210, 211, 1, true);

-- MailActivation : UPLOAD_REQUEST_FILE_DELETED_BY_SENDER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (215, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (216, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (217, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(27, false, 'UPLOAD_REQUEST_FILE_DELETED_BY_SENDER', 215, 216, 217, 1, true);

-- MailActivation : SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (218, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (219, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (220, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(28, false, 'SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER', 218, 219, 220, 1, true);

-- MailActivation : UNDOWNLOADED_SHARED_DOCUMENTS_ALERT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (221, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (222, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (223, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(29, false, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT', 221, 222, 223, 1, true);

-- MailActivation : END

-- ###END-PART-1###
-- ###BEGIN-PART-2###




INSERT INTO mail_layout (id, domain_abstract_id, description, visible, layout, creation_date, modification_date, uuid, readonly, messages_french, messages_english) VALUES (1, 1, 'Default HTML layout', true, '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <body>

    <div data-th-fragment="header">
    <title data-th-text="${mailSubject}">Mail subject</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta http-equiv="Content-Style-Type" content="text/css" />
        <style type="text/css">
     pre { margin-top: .25em; font-family: Verdana, Arial, Helvetica, sans-serif; color: blue; }
      ul { margin-top: .25em; padding-left: 1.5em; }
      </style>
    </div>

    <div data-th-fragment="logo">
         <img th:src="#{logoLinShareBase64}"/>
    </div>

    <div data-th-fragment="arrow">
         <img src=''data:image/png;base64, iVBORw0KGgoAAAANSUhEUgAAABQAAAAJCAYAAAAywQxIAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAABmJLR0QAAAAAAAD5Q7t/AAAACXBIWXMAAAsSAAALEgHS3X78AAABWklEQVQoz52Q3UvCYBSHz0ZO3LtNL5KsiJTMfTSjoKsglKD+6IIypKDwJvY9E6KwArtQt72bzbmuLCvt69ye8zznx4+o189Pswu56vpaoUuSBPxnRnEMdrOVcR2nRlxeNZaBIE5omi5JAg9/lb6EIdh2Cxynb9M0vU8AACiqnnEc55hLp3ckkYffKsNhBLpuAsZeI5VKHWxvbXbf2GtFm8cY19McJ/B8CX4KGg0j0AwT/CAwEUJ75Q3xGQA+hlENK+v0+zWOS0sCXwSSmG4NwyEYhgX+INAZlq3KIt8Z78jJQ1nkOyzLVnq9rmJZzenJohEYpgU48BWGYSqTsi8Jx6NoxqLnuhcIobwk8kCS739VzQDP824Ry+yWJfHxMzuzKVU3lzzXPUsmk8VCfhXiOIa7dht87N8ghCqyJDxM476tXjWs3CAIjnyMVyiKgrlE4p6iqENZEp5mMa+E9JG/m4wFNQAAAABJRU5ErkJggg=='' />
    </div>

    <div data-th-fragment="libre-free">
         <img src=''data:image/png;base64, iVBORw0KGgoAAAANSUhEUgAAADwAAAAJCAYAAABuS09sAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAABmJLR0QAAAAAAAD5Q7t/AAAACXBIWXMAAAsSAAALEgHS3X78AAACHUlEQVRIx92UP2tTYRTGfylZtd6iDo4XxMFJUvQLpIuz6aLgMyUfIakfoJhJXAQ7yAMukqCDIIgJCi4u0Q+gmE0Q/zR2cFKsQ85bbq+3te3YA5d7z3nve/485zmnZvsMcAs4DZwAloGTwGegI+kVx0hqtq8Cz0JfB+4CZ4E14BLQBV5K+lm+bHsCrEiahd4FcuAtkEnqHzYh2y1gAMyAZUnTirMkM0lLh/FfB04BH4A3wHoU9gW4bvsb8BR4DFyruJ+lYgFSgbZvR9FHkTawCjSAJrBROMuB3lGALBa8BryXdLOE9CIwBX4DXys6kcV50TaIBBtAO+mSOrZHYZ8GGG0quhjn7QCwVwqbA8NSzD39xi8TIEt5LACLzOd3l0jaYk7xj0A5MIF+uYtNSeMIsAosAa1Csh2gH//Vwm+3wm8uaaUiZgMY2d62vX0AvwPme6gGtGxnC8Aj4IrtixUBHgDfo/iy5OUOB7JEwuOge7JlkoaRdB4J3w9wigzpAH3bXduTYNJOwZJq6TmA3wYwCFsG5PVA4xzwxPYP4B3wArgHbEWnqmQXvWw3gKntZgLCdrtgGxdA2WsO83gPgU3mNEwLMSsz6n9+bW+Wl1pd0h/gRqDbAi4D54HnQFvSr30KHtlOei8SygtIp1kq0n8DmMRimzLf8okpQ2AU3+O4t9Nd/h2hfD+/wZRE/Z6kfqIFti8Ad4DXwENJnziG8hdyJvydlDef+gAAAABJRU5ErkJggg'' />
    </div>

  </body>
</html>', now(), now(), '15044750-89d1-11e3-8d50-5404a683a462', true, 'productName=LinShare
productCompagny=Linagora
logoLinShareBase64=data:image/png;base64, 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', 'productName=LinShare
productCompagny=Linagora
logoLinShareBase64=data:image/png;base64, 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');



INSERT INTO mail_config (id, mail_layout_id, domain_abstract_id, name, visible, uuid, creation_date, modification_date, readonly) VALUES (1, 1, 1, 'Default mail config', true, '946b190d-4c95-485f-bfe6-d288a2de1edd', now(), now(), false);



INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (4, 1, 'Password reset', true, 3, 'Your password has been reset', 'Your LinShare account:<ul><li>Login: <code>${mail}</code> &nbsp;(your e-mail address)</li><li>Password: <code>${password}</code></li></ul><br/>', '753d57a8-4fcc-4346-ac92-f71828aca77c', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (2, 1, 'Registered user downloaded a file', true, 1, 'A user ${actorRepresentation} has just downloaded a file you made available for sharing', '${recipientFirstName} ${recipientLastName} has just downloaded the following file(s) you made available to her/him via LinShare:<ul>${documentNames}</ul><br/>', '403e5d8b-bc38-443d-8b94-bab39a4460af', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (3, 1, 'New guest', true, 2, 'Your LinShare account has been sucessfully created', '<strong>${ownerFirstName} ${ownerLastName}</strong> invites you to use and enjoy LinShare!<br/><br/>To login, please go to: <a href="${url}">${url}</a><br/><br/>Your LinShare account:<ul><li>Login: <code>${mail}</code> &nbsp;(your e-mail address)</li><li>Password: <code>${password}</code></li></ul><br/>', 'a1ca74a5-433d-444a-8e53-8daa08fa0ddb', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (5, 1, 'Shared document was updated', true, 4, 'A user ${actorRepresentation} has just modified a shared file you still have access to', '<strong>${firstName} ${lastName}</strong> has just modified the following shared file <strong>${fileOldName}</strong>:<ul><li>New file name: ${fileName}</li><li>File size: ${fileSize}</li><li>MIME type: <code>${mimeType}</code></li></ul><br/>To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>', '09a50c58-b430-4ccf-ab3e-0257c463d8df', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (6, 1, 'Shared document was deleted', true, 5, 'A user ${actorRepresentation} has just deleted a shared file you had access to!', '<strong>${firstName} ${lastName}</strong> has just deleted a previously shared file <strong>${documentName}</strong>.<br/>', '554a3a2b-53b1-4ec8-9462-2d6053b80078', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (7, 1, 'Shared document is soon to be outdated', true, 6, 'A LinShare workspace is about to be deleted', 'Your access to the shared file ${documentName}, granted by ${firstName} ${lastName}, will expire in ${nbDays} days. Remember to download it before!<br/>To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>', 'e7bf56c2-b015-4e64-9f07-3c7e2f3f9ca8', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (8, 1, 'Document is soon to be outdated', true, 7, 'A shared file is about to be deleted!', 'Your access to the file <strong>${documentName}</strong> will expire in ${nbDays} days!<br/>To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>', '1507e9c0-c1e1-4e0f-9efb-506f63cbba97', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (10, 1, 'New sharing with password protection', true, 9, 'A user ${actorRepresentation} has just made a file available to you!', '<strong>${firstName} ${lastName}</strong> has just shared with you ${number} file(s):<ul>${documentNames}</ul><br/>To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>The password to be used is: <code>${password}</code><br/><br/>', '1e972f43-619c-4bd6-a1bd-10667b80af74', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (11, 1, 'New sharing of a cyphered file', true, 10, 'A user ${actorRepresentation} has just made a file available to you!', '<strong>${firstName} ${lastName}</strong> has just shared with you ${number} file(s):<ul>${documentNames}</ul><br/>To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/><p>One or more received files are <b>encrypted</b>. After download is complete, make sure to decrypt them locally by using the application:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>You must use the <i>password</i> granted to you by the user who made the file(s) available for sharing.</p><br/><br/>', 'fef9a3f1-6011-46cd-8d39-6bd1bc02f899', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (12, 1, 'New sharing with password protection of a cyphered file', true, 11, 'A user ${actorRepresentation} has just made a file available to you!', '<strong>${firstName} ${lastName}</strong> has just shared with you ${number} file(s):<ul>${documentNames}</ul><br/>To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/><p>One or more received files are <b>encrypted</b>. After download is complete, make sure to decrypt them locally by using the application:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>You must use the <i>password</i> granted to you by the user who made the file(s) available for sharing.</p><br/><br/>The password to be used is: <code>${password}</code><br/><br/>', '2da85945-7793-43f4-b547-eacff15a6f88', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (13, 1, 'New upload proposition', true, 12, 'A user ${actorRepresentation} has send to you an upload proposition: ${subject}', '<strong>${firstName} ${lastName}</strong> has just send to you an upload request: ${subject}<br/>${body}<br/>You need to activate or reject this request <br/><br/>', 'dd7d6a36-03b6-48e8-bfb5-3c2d8dc227fd', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (14, 1, 'Upload proposition rejected', true, 13, 'A user ${actorRepresentation} has rejected your upload proposition: ${subject}', '<strong>${firstName} ${lastName}</strong> has just rejected your upload proposition: ${subject}<br/>${body}<br/><br/>', '62af93dd-0b19-4376-bc76-08b7a97fc0f2', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (15, 1, 'Upload request updated', true, 14, 'A user ${actorRepresentation} has updated upload request: ${subject}', '<strong>${firstName} ${lastName}</strong> has just updated the upload request: ${subject}<br/>${body}<br/>New settings can be found here: <a href="${url}">${url}</a><br/><br/>', '40f36a3b-39ea-4723-a292-9c86e2ee8f94', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (16, 1, 'Upload request activated', true, 15, 'A user ${actorRepresentation} has activated upload request: ${subject}', '<strong>${firstName} ${lastName}</strong> has just activate the upload request: ${subject}<br/>${body}<br/>To upload files, simply click on the following link or copy/paste it into your favorite browser: <a href="${url}">${url}</a><br/><p>Upload request may be <b>encrypted</b>, use <em>password</em>: <code>${password}</code><br/><br/>', '817ae032-9022-4c22-97a3-cfb5ce50817c', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (17, 1, 'Upload proposition filtered', true, 16, 'An upload proposition has been filtered: ${subject}', 'A new upload proposition has been filtered.<br/>Subject: ${subject}<br/>${body}<br/><br/>', 'd692674c-e797-49f1-a415-1df7ea5c8fee', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (18, 1, 'Upload request created', true, 17, 'A user ${actorRepresentation} has created upload request: ${subject}', '<strong>${firstName} ${lastName}</strong> has just made you an upload request: ${subject}.<br/>${body}<br/>It will be activated ${activationDate}<br/><br/>', '40a74e4e-a663-4ad2-98ef-1e5d70d3536c', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (19, 1, 'Upload request acknowledgement', true, 18, 'A user ${actorRepresentation} has upload a file for upload request: ${subject}', '<strong>${firstName} ${lastName}</strong> has upload a file.<br/>File name: ${fileName}<br/>Deposit date: ${depositDate}<br/>File size: ${fileSize}<br/><br/>', '5ea27e5b-9260-4ce1-b1bd-27372c5b653d', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (20, 1, 'Upload request reminder', true, 19, 'A user ${actorRepresentation} reminds you have an upload request: ${subject}', '<strong>${firstName} ${lastName}</strong> reminds you have got an upload request : ${subject}.<br/>${body}<br/>To upload files, simply click on the following link or copy/paste it into your favorite browser: <a href="${url}">${url}</a><br/><p>Upload request may be <b>encrypted</b>, use <em>password</em>: <code>${password}</code><br/><br/><br/><br/>', '0d87e08d-d102-42b9-8ced-4d49c21ce126', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (21, 1, 'Upload request will be expired', true, 20, 'The upload request: ${subject}, will expire', 'Expiry date approaching for upload request: ${subject}<br/>${body}<br/>Be sure that the request is complete<br/>Files already uploaded: ${files}<br/><br/>', 'd43b22d6-d915-41cc-99e4-9c9db66c5aac', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (22, 1, 'Upload request will be expired', true, 21, 'The upload request: ${subject}, will expire', 'Expiry date approaching for upload request: ${subject}<br/>${body}<br/>Files already uploaded: ${files}<br/>To upload files, simply click on the following link or copy/paste it into your favorite browser: <a href="${url}">${url}</a><br/><br/>', '0bea7e7c-e2e9-44ff-bbb3-7e28967a4d67', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (23, 1, 'Upload request is expired', true, 22, 'The upload request: ${subject}, is expired', 'Expiration of the upload request: ${subject}<br/>${body}<br/>Files uploaded: ${files}<br/><br/>', '0cd705f3-f1f5-450d-bfcd-f2f5a60c57f8', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (24, 1, 'Upload request is expired', true, 23, 'The upload request: ${subject}, is expired', 'Expiration of the upload request: ${subject}<br/>${body}<br/>Files uploaded: ${files}<br/>You will not be able to upload file anymore<br/><br/>', '7412940b-870b-4f58-877c-9955a423a5f3', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (25, 1, 'Upload request closed', true, 24, 'A user ${actorRepresentation} has just closed upload request: ${subject}', '<strong>${firstName} ${lastName}</strong> has just closed the upload request: ${subject}<br/>${body}<br/>Files uploaded: ${files}<br/><br/>', '6c0c1214-0a77-46d0-92c5-c41d225bf9aa', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (26, 1, 'Upload request closed', true, 25, 'A user ${actorRepresentation} has just closed upload request: ${subject}', '<strong>${firstName} ${lastName}</strong> has just closed the upload request: ${subject}<br/>${body}<br/>Files uploaded: ${files}<br/>You will not be able to upload file anymore<br/><br/>', '1956ca27-5127-4f42-a41d-81a72a325aae', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (27, 1, 'Upload request deleted', true, 26, 'A user ${actorRepresentation} has just deleted an upload request', '<strong>${firstName} ${lastName}</strong> has just deleted the upload request: ${subject}<br/>${body}<br/>You will not be able to upload file anymore<br/><br/>', '690f1bbc-4f99-4e70-a6cd-44388e3e2c86', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (28, 1, 'Upload request error: no space left', true, 27, 'A user ${actorRepresentation} has just tried to upload a file but server had no space left', '<strong>${firstName} ${lastName}</strong> has just tried to upload in the upload request: ${subject}<br/>${body}<br>Please free space and notify the recipient to retry is upload<br/><br/>', '48fee30b-b2d3-4f85-b9ee-22044f9dbb4d', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (30, 1, 'Upload request file deleted', true, 29, 'A user ${actorRepresentation} has deleted a file for upload request: ${subject}', '<strong>${firstName} ${lastName}</strong> has deleted a file.<br/>File name: ${fileName}<br/>Deletion date: ${deleteDate}<br/>File size: ${fileSize}<br/><br/>', '88b90304-e9c9-11e4-b6b4-5404a6202d2c', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (31, 1, 'Share creation acknowledgement', true, 30, '[SHARE ACKNOWLEDGEMENT] Shared on ${date}.', 'You just shared ${fileNumber} file(s), on the ${creationDate}, expiring the ${expirationDate}, with :<br/><ul>${recipientNames}</ul><br/>The list of your files is : <ul>${documentNames}</ul><br/>', '01e0ac2e-f7ba-11e4-901b-08002722e7b1', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (32, 1, 'Share creation acknowledgement', true, 31, '[SHARE ACKNOWLEDGEMENT] ${subject}. Shared on ${date}.', 'You just shared ${fileNumber} file(s), on the ${creationDate}, expiring the ${expirationDate}, with :<br/><ul>${recipientNames}</ul><br/>Your original message was:<br/><i>${message}</i><br/><br/>The list of your files is : <ul>${documentNames}</ul><br/>', '2209b038-e1e7-11e4-8d2d-3b2a506425c0', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (33, 1, 'Undownloaded shared documents alert', true, 32, '[Undownloaded shared documents alert] ${subject} Shared on ${date}.', 'Please find below the resume of the share you made on ${creationDate} with initial expiration date on ${expirationDate}.<br /> List of documents : <br /><table style="border-collapse: collapse;">${shareInfo}</table><br/>', 'eb291876-53fc-419b-831b-53a480399f7c', now(), now(), true, NULL, NULL);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (1, 1, 'Anonymous user downloaded a file', true, 0, '[(#{subject(${shareRecipient.mail})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<header>
     <div data-th-insert="layout :: header"></div>
</header>
  <body>

<div data-th-object="${shareOwner}">
    <p data-th-utext="#{welcomeMessage(${shareOwner.firstName},${shareOwner.lastName})}"> Welcome Peter Wilson,</p>
    <p>Bonjour <span data-th-text="*{firstName}">Peter</span> <span data-th-text="*{lastName}">Wilson</span>.</p>
 </div>



<br />
  <div data-th-insert="footer :: footer"></div>

An unknown user ${email} has just downloaded the following file(s) you made available via LinShare:<ul>${documentNames}</ul><br/>
<!-- fin du code moisi de fred --> 


<div bgcolor="#ffffff" marginheight="0" marginwidth="0"
     style="width:100%!important;margin:0;padding:0;background-color:#ffffff" width="100% !important">
  <center>
    <table bgcolor="#ffffff" border="0" cellpadding="0" cellspacing="0" height="100% !important"
           style="height:100%!important;margin:0;padding:0;background-color:#ffffff;width:90%;max-width:450px"
           width="90%">
      <tbody>
      <tr>
        <td align="center" style="border-collapse:collapse" valign="top">
          <div
            style="margin-top:10px;font-weight:100;margin-bottom:0px;float:right;margin-right:10%;font-family:Helvetica,Arial,sans-serif"></div>
          <table border="0" cellpadding="0" cellspacing="0" style="border:0px;width:90%;max-width:500px" width="90%">
            <tbody>
            <tr>
              <td align="center" style="border-collapse:collapse" valign="top">
                <table bgcolor="transparent" border="0" cellpadding="0" cellspacing="0"
                       style="background-color:transparent;border-bottom:0;padding:0px">
                  <tbody>
                  <tr>
                    <td align="center" bgcolor="#ffffff"
                        style="border-collapse:collapse;color:#202020;background-color:#ffffff;font-family:Arial;font-size:34px;font-weight:bold;line-height:100%;padding:0;text-align:center;vertical-align:middle">
                      <div align="center" style="text-align:center">
                        <a href="" title="LinShare homepage"  target="_blank">

<img src=''data:image/png;base64, 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
''                                 style="border:0;line-height:100%;outline:none;text-decoration:none;width:198px;height:49px;padding:20px 0 20px 0"
                                width="198" />

<!--
                          <img alt="Logo LinShare english" height="49" src="img/email-logo-linshare-en.png"
                                style="border:0;line-height:100%;outline:none;text-decoration:none;width:198px;height:49px;padding:20px 0 20px 0"
                                width="198" />
-->
                        </a>
                      </div>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </td>
            </tr>
            <tr>
              <td align="center" style="border-collapse:collapse" valign="top">
                <table border="0" cellpadding="0" cellspacing="0" style="width:95%;max-width:500px" width="95%">
                  <tbody>
                  <tr>
                    <td
                      style="border-collapse:collapse;border-radius:3px;font-weight:300;border:1px solid #e1e1e1;background:white"
                      valign="top">
                      <table border="0" cellpadding="20" cellspacing="0" width="100%">
                        <tbody>
                        <tr>
                          <td style="border-collapse:collapse;padding:0px" valign="top">
                            <div align="left"
                                 style="color:#505050;font-family:Helvetica,Arial;font-size:14px;line-height:150%;text-align:left">
                              <div align="left"
                                   style="padding:24px 17px;font-family:Helvetica,Arial, sans-serif;line-height: 21px;margin:0px;text-align:left;font-size: 13px;">
                               

    <p style="color:#505050;margin-top:0;font-weight:300;margin-bottom:10px" data-th-utext="#{welcomeMessage(${shareOwner.firstName},${shareOwner.lastName})}"> Welcome Peter Wilson,</p> 

                                <p style="margin:0">The external recipient <b>han.solo@domain.com</b></a></p>
                                <span>has downloaded your file: </span>
                                <span>
                                  <a target="_blank" style="color:#1294dc;text-decoration:none;" href="http://www.linshare.org/" title="LinShare">
                                      watch-me.mov
                                  </a>
                                </span>
                              </div>
                            </div>
                            <table border="0" cellspacing="0" cellpadding="0" width="100%"
                                   style="background-color: #f8f8f8;">
                              <tbody>
                              <tr>
                                <td width="15" style="border-top:1px solid #c9cacc;">
                                  <img src="img/space.gif" width="1" height="1" border="0" style="display:block;"></td>
                                <td width="20"><img src="img/arrow.png" width="20" height="9" border="0"
                                                    style="display:block;"></td>
                                <td style="border-top:1px solid #c9cacc;"><img src="img/space.gif" width="1" height="1"
                                                                               border="0" style="display:block;"></td>
                              </tr>
                              </tbody>
                            </table>
                            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                              <tbody>
                              <tr>
                                <td>
                                  <div align="left"
                                       style="font-family:Helvetica,Arial;font-size:14px;padding: 0px 17px;background: #f8f8f8;text-align:left;color:#7f7f7f;line-height:20px;">
                                    <div align="left"
                                         style="font-family:Helvetica,sans-serif;font-size:13px;line-height:20px;margin:0;padding: 15px 0 20px;">
                                      <div style="margin-bottom:17px;">
                                                    <span style="font-weight:bold;">
                                                   Shared the
                                                    </span>
                                        <br>
                                        7th of November, 2017
                                        <br>
                                      </div>
                                      <div style="margin-bottom: 17px;">
                                                    <span style="font-weight:bold;">
                                                   Available until the
                                                    </span>
                                        <br>
                                        7th of December, 2017
                                        <br>
                                      </div>
                                      <div>
                                                    <span style="font-weight:bold;">
                                                   Files associated with the share
                                                    </span>
                                        <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
                                          <li style="color:#00b800;font-size:15px"><span
                                            style="color:#7f7f7f;font-size:13px">test-file.jpg</span></li>
                                          <li style="color:#00b800;font-size:15px"><span
                                            style="color:#7f7f7f;font-size:13px;font-weight:bold;">watch-me.mov</span>
                                          </li>
                                          <li style="color:#787878;font-size:15px"><span
                                            style="color:#7f7f7f;font-size:13px">presentation-test.jpg</span></li>
                                          <li style="color:#00b800;font-size:15px"><span
                                            style="color:#7f7f7f;font-size:13px">mon-non-de-fichier.ppt</span></li>
                                          <li style="color:#00b800;font-size:15px"><span
                                            style="color:#7f7f7f;font-size:13px">clip.jpg</span></li>
                                        </ul>
                                      </div>
                                    </div>
                                </td>
                              </tr>
                              </tbody>
                            </table>
                            <table width="100%"
                                   style="font-family:Helvetica,Arial;padding:0 17px;background:#f0f0f0;text-align:left;color:#a9a9a9;line-height:20px;border-top:1px solid #e1e1e1">
                              <tbody>
                              <tr>
                                <td style="border-collapse:collapse;padding: 4px 0" valign="top">
                                  <p style="margin:0;font-size: 9px;">Learn more about <a href="http://www.linshare.org/" title="LinShare homepage" target="_blank"
                                                                                          style="text-decoration:none; color:#a9a9a9;"><strong>LinShare</strong>™</a>
                                  </p><a href="" style="text-decoration:none; color:#a9a9a9;">
                                </a></td>
                                <td style="border-collapse:collapse;padding:4px 0px" valign="top" width="60">
                                  <img alt="libre-and-free" height="9" src="img/email-libre-free.png"
                                       style="line-height:100%;width:60px;height:9px;padding:0" width="60">
                                </td>
                              </tr>
                              </tbody>
                            </table>
                          </td>
                        </tr>
                        </tbody>
                      </table>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </td>
            </tr>
            <tr>
              <td align="center" style="border-collapse:collapse" valign="top">
                <table bgcolor="white" border="0" cellpadding="10" cellspacing="0"
                       style="background-color:white;border-top:0" width="400">
                  <tbody>
                  <tr>
                    <td style="border-collapse:collapse" valign="top">
                      <table border="0" cellpadding="10" cellspacing="0" width="100%">
                        <tbody>
                        <tr>
                          <td bgcolor="#ffffff" colspan="2"
                              style="border-collapse:collapse;background-color:#ffffff;border:0;padding: 0 8px;"
                              valign="middle">
                            <div align="center"
                                 style="color:#707070;font-family:Arial;font-size:12px;line-height:125%;text-align:center">
                              <p
                                style="line-height:15px;font-weight:300;margin-bottom:0;color:#b2b2b2;font-size:10px;margin-top:0">
                                You are using the Open Source and free version of
                                <a><strong>LinShare</strong>™, powered by Linagora ©&nbsp;2009–2015.Contribute to
                                  Linshare R&amp;D by subscribing to an Enterprise offer.
                                </a></p><a>
                            </a></div>
                            <a>
                            </a></td>
                        </tr>
                        <tr>
                          <td style="border-collapse:collapse" valign="top" width="350">
                            <div align="left"
                                 style="color:#707070;font-family:Arial;font-size:12px;line-height:125%;text-align:left"></div>
                          </td>
                          <td style="border-collapse:collapse" valign="top" width="190">
                            <div align="left"
                                 style="color:#707070;font-family:Arial;font-size:12px;line-height:125%;text-align:left"></div>
                          </td>
                        </tr>
                        <tr>
                          <td bgcolor="#ffffff" colspan="2"
                              style="border-collapse:collapse;background-color:#ffffff;border:0" valign="middle">
                            <div align="center"
                                 style="color:#707070;font-family:Arial;font-size:12px;line-height:125%;text-align:center"></div>
                          </td>
                        </tr>
                        </tbody>
                      </table>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </td>
            </tr>
            </tbody>
          </table>
        </td>
      </tr>
      </tbody>
    </table>
  </center>
</div>

 </body>
 </html>', '938f91ab-b33c-4184-900f-c8a595fc6cd9', now(), now(), true, 'welcomeMessage=[FR] Hello fr {0} {1},
subject=[Translate in french] An unknown user {0} has just downloaded a file you made available for sharing.', 'subject=An unknown user {0} has just downloaded a file you made available for sharing.
welcomeMessage=[EN]Hello {0} {1},
');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (9, 1, 'New sharing', true, 8, 'A user ${actorRepresentation} has just made a file available to you!', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<header>
     <div data-th-insert="layout :: header"></div>
</header>
  <body>
<br />
<div data-th-object="${shareOwner}">
    <p>First name: <span data-th-text="*{firstName}">Sebastian</span>.</p>
    <p>Last name: <span data-th-text="*{lastName}">Pepper</span>.</p>
 </div>
<br />

  <div data-th-insert="layout :: logo"></div>

  <div data-th-insert="layout :: libre-free"></div>

  <div data-th-insert="layout :: arrow"></div>

  <div data-th-insert="footer :: footer"></div>
 </body>
 </html>', '250e4572-7bb9-4735-84ff-6a8af93e3a42', now(), now(), true, NULL, NULL);



INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (1, 0, 1, 1, 0, 'd6868568-f5bd-4677-b4e2-9d6924a58871', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (2, 0, 2, 1, 1, '4f3c4723-531e-449b-a1ae-d304fd3d2387', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (3, 0, 3, 1, 2, '81041673-c699-4849-8be4-58eea4507305', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (4, 0, 4, 1, 3, '85538234-1fc1-47a2-850d-7f7b59f1640e', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (5, 0, 5, 1, 4, '796a98eb-0b97-4756-b23e-74b5a939c2e3', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (6, 0, 6, 1, 5, 'ed70cc00-099e-4c44-8937-e8f51835000b', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (7, 0, 7, 1, 6, 'f355793b-17d4-499c-bb2b-e3264bc13dbd', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (8, 0, 8, 1, 7, '5a6764fc-350c-4f10-bdb0-e95ca7607607', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (10, 0, 10, 1, 9, 'fa59abad-490b-4cd5-9a31-3c3302fc4a18', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (11, 0, 11, 1, 10, '5bd828fa-d25e-47fa-9c0d-1bb84304e692', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (12, 0, 12, 1, 11, 'a9096a7e-949c-4fae-aedf-2347c40cd999', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (13, 0, 13, 1, 12, '1216ca54-f510-426c-a12b-8158efa21619', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (14, 0, 14, 1, 13, '9f87c53d-80e5-4e10-b571-d0c9f9c35017', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (15, 0, 15, 1, 14, '454e3e88-7129-4e98-a79a-e119cb94bd07', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (16, 0, 16, 1, 15, '0a8251dd-9514-4b7b-bf47-c398c00ba21b', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (17, 0, 17, 1, 16, 'e3b99efb-875c-4c63-bd5c-8f121d75876b', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (18, 0, 18, 1, 17, 'e37cbade-db93-487d-96ee-dc491ce63035', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (19, 0, 19, 1, 18, '8d707581-3920-4d82-a8ba-f7984afc54ca', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (20, 0, 20, 1, 19, '64b5df7b-b197-49a7-b0af-aaac2c2f8d79', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (21, 0, 21, 1, 20, 'fd6011cf-e4cf-478d-835b-75b25e024b81', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (22, 0, 22, 1, 21, 'e4439f5b-380b-4a78-86a7-764f15ff599d', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (23, 0, 23, 1, 22, '7a560359-fa35-4ffd-ac1d-1d9ceef1b1e0', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (24, 0, 24, 1, 23, '2b038721-fe6e-4406-b5de-c4c84a964df8', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (25, 0, 25, 1, 24, '822b3ede-daea-4b60-a8a2-2216c7d36fea', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (26, 0, 26, 1, 25, 'd8316b6b-f6c8-408b-ac7d-1ebea767912e', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (27, 0, 27, 1, 26, '7642b888-3bd8-4f8c-b65c-81b61e512137', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (28, 0, 28, 1, 27, '9bf9d474-fd10-48da-843c-dfadebd2b455', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (30, 0, 30, 1, 29, 'ec270da7-e9cb-11e4-b6b4-5404a6202d2c', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (31, 0, 31, 1, 30, '447217e4-e1ee-11e4-8a45-fb8c68777bdf', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (32, 0, 32, 1, 31, '1837a6f0-e8c7-11e4-b36a-08002722e7b1', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (33, 0, 33, 1, 32, 'bfcced12-7325-49df-bf84-65ed90ff7f59', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (9, 0, 9, 1, 8, 'befd8182-88a6-4c72-8bae-5fcb7a79b8e7', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (51, 1, 1, 1, 0, 'd0af96a7-6a9c-4c3f-8b8c-7c8e2d0449e1', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (52, 1, 2, 1, 1, '28e5855a-c0e7-40fc-8401-9cf25eb53f03', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (53, 1, 3, 1, 2, '41d0f03d-57dd-420e-84b0-7908179c8329', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (54, 1, 4, 1, 3, '72c0fff4-4638-4e98-8223-df27f8f8ea8b', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (55, 1, 5, 1, 4, '8b7f57c1-b4a1-4896-8e19-d3ebf3af4831', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (56, 1, 6, 1, 5, '6fbabf1a-58c0-49b9-859e-d24b0af38c87', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (57, 1, 7, 1, 6, 'b85fc62f-d9eb-454b-9289-fec5eab51a76', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (58, 1, 8, 1, 7, '25540d2d-b3b8-46a9-811b-0549ad300fe0', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (59, 1, 9, 1, 8, '72ae03e7-5865-433c-a2be-a95c655a8e17', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (60, 1, 10, 1, 9, 'e2af2ff6-585b-4cdc-a887-1755e42fcde6', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (61, 1, 11, 1, 10, '1ee1c8bc-75e9-4fbe-a34b-893a86704ec9', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (62, 1, 12, 1, 11, '12242aa8-b75e-404d-85df-68e7bb8c04af', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (63, 1, 13, 1, 12, '4f2ad41c-3969-461d-a6dc-8f692a1738e9', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (64, 1, 14, 1, 13, '362cf576-30ab-41a5-85d0-3d9175935b14', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (65, 1, 15, 1, 14, '35b81d85-0ee7-44f9-b478-20c8429c2b6d', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (66, 1, 16, 1, 15, '92e0a55e-e4e8-43c9-94f0-0d4e74d5748f', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (67, 1, 17, 1, 16, 'eb8a1b1e-758d-4261-8616-8ead644f70b0', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (68, 1, 18, 1, 17, '50ae2621-556c-446d-a399-55ed799022c3', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (69, 1, 19, 1, 18, '6580009b-36fd-472d-9937-41d0097ead91', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (70, 1, 20, 1, 19, 'ed471d9b-6f64-4d36-97cb-654b73579fe9', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (71, 1, 21, 1, 20, '86fdc43c-5fd7-4aba-b01a-90fccbfb5489', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (72, 1, 22, 1, 21, 'ea3f9814-6da9-49bf-94e5-7ff2c789e07b', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (73, 1, 23, 1, 22, 'f9455b1d-3582-4998-8675-bc0a8137fc73', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (74, 1, 24, 1, 23, '8f91e46b-1cee-45bc-8712-23ea0298db87', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (75, 1, 25, 1, 24, 'e5a9f689-c005-47c2-958f-b68071b1bf6f', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (76, 1, 26, 1, 25, 'a7994bd1-bd67-4cc6-93f3-be935c1cdb67', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (77, 1, 27, 1, 26, '5e1fb460-1efc-497c-96d8-6adf162cbc4e', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (78, 1, 28, 1, 27, '2daaea2a-1b13-48b4-89a6-032f7e034a2d', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (80, 1, 30, 1, 29, 'd6e18c3b-e9cb-11e4-b6b4-5404a6202d2c', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (81, 1, 31, 1, 30, '8f579a8a-e352-11e4-99b3-08002722e7b1', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (82, 1, 32, 1, 31, '2d3a0e80-e8c7-11e4-8349-08002722e7b1', false);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (83, 1, 33, 1, 32, 'fa7a23cb-f545-45b4-b9dc-c39586cb2398', false);



INSERT INTO mail_footer (id, domain_abstract_id, description, visible, footer, creation_date, modification_date, uuid, readonly, messages_french, messages_english) VALUES (1, 1, 'footer html', true, '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <body>
    <div data-th-fragment="footer">
     <a href="http://linshare.org/" title=""><strong><span  data-th-text="#{productName}"> productName</span></strong></a> - THE Secure, Open-Source File Sharing Tool
   </div>
 </body>
 </html>', now(), now(), 'e85f4a22-8cf2-11e3-8a7a-5404a683a462', true, '', '');



INSERT INTO mail_footer_lang (id, mail_config_id, mail_footer_id, language, uuid, readonly) VALUES (1, 1, 1, 0, 'bf87e580-fb25-49bb-8d63-579a31a8f81e', false);
INSERT INTO mail_footer_lang (id, mail_config_id, mail_footer_id, language, uuid, readonly) VALUES (2, 1, 1, 1, 'a6c8ee84-b5a8-4c96-b148-43301fbccdd9', false);



-- ###END-PART-2###
-- ###BEGIN-PART-3###

UPDATE domain_abstract SET mailconfig_id = 1;
UPDATE mail_footer SET readonly = true;
UPDATE mail_layout SET readonly = true;
UPDATE mail_content SET readonly = true;

-- LinShare version
INSERT INTO version (id, version) VALUES (1, '1.12.0');

-- Sequence for hibernate
SELECT setval('hibernate_sequence', 1000);





-- Alias
CREATE VIEW alias_func_list_all  AS SELECT
 functionality.id, functionality.system as sys, identifier, policy_delegation_id AS pd_id, domain_id, param, parent_identifier AS parent,
 ap.status AS ap_status, ap.default_status AS ap_default, ap.policy AS ap_policy, ap.system AS ap_sys,
 cp.status AS cp_status, cp.default_status AS cp_default, cp.policy AS cp_policy, cp.system AS cp_sys
 FROM functionality
 JOIN policy AS ap ON policy_activation_id = ap.id
 JOIN policy AS cp ON policy_configuration_id = cp.id order by identifier;

-- Alias for Users
-- All users
CREATE VIEW alias_users_list_all AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id;
-- All active users
CREATE VIEW alias_users_list_active AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed = 0;
-- All destroyed users
CREATE VIEW alias_users_list_destroyed AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed = 0;

-- Alias for threads
-- All threads
CREATE VIEW alias_threads_list_all AS SELECT a.id, mail, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id;
-- All active threads
CREATE VIEW alias_threads_list_active AS SELECT a.id, mail, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed = 0;
-- All destroyed threads
CREATE VIEW alias_threads_list_destroyed AS SELECT a.id, mail, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed >= 1;

COMMIT;
-- ###END-PART-3###
