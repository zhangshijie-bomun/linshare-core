INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, domain_quota, ensemble_quota,current_value, last_value, 
domain_id, account_id, parent_domain_id, quota, quota_warning, file_size_max, ensemble_type, quota_type)
VALUES (1, 'aebe1b64-39c0-11e5-9fa8-080027b8274b', NOW(), NOW(), NOW(), null, null,1096,500,3,null,null, 1900, 1800, 5, null, 'DOMAIN_QUOTA'), 
(2, 'aebe1b64-39c0-11e5-9fa8-080027b8274c', NOW(), NOW(), NOW(), 1, null,496,0,3,null,2, 1900, 1300, 5, 'USER', 'ENSEMBLE_QUOTA'),
(3, 'aebe1b64-39c0-11e5-9fa8-080027b8274e', NOW(), NOW(), NOW(), 1, null,900,200,3,null,2, 2000, 1500, 5, 'THREAD', 'ENSEMBLE_QUOTA'),
(4, 'aebe1b64-39c0-11e5-9fa8-080027b8274d', NOW(), NOW(), NOW(), null, null,1096,500,2,null,null, 100000, 100000, 10000, null, 'DOMAIN_QUOTA'), 
(5, 'aebe1b64-39c0-11e5-9fa8-080027b8274f', NOW(), NOW(), NOW(), 4, null,496,0,2,null,1, 100000, 100000, 10000, 'USER', 'ENSEMBLE_QUOTA'),
(6, 'aebe1b64-39c0-11e5-9fa8-080027b8274a', NOW(), NOW(), NOW(), 4, null,900,200,2,null,1, 2000, 1500, 5, 'THREAD', 'ENSEMBLE_QUOTA'),
(7, 'aebe1b64-39c0-11e5-9fa8-080027b8274g', NOW(), NOW(), NOW(), null, null, 1096,100, 1, null, null, 100000, 100000, 100000, null, 'PLATFORM_QUOTA'),
(8, 'aebe1b64-39c0-11e5-9fa8-080027b8274z', NOW(), NOW(), NOW(), 7, null,496,0,1,null,null, 1900, 1300, 5, 'USER', 'ENSEMBLE_QUOTA'),
(9, 'aebe1b64-39c0-11e5-9fa8-080027b8274h', NOW(), NOW(), NOW(), 7, null,900,200,1,null,null, 2000, 1500, 5, 'THREAD', 'ENSEMBLE_QUOTA'),
(10, 'aebe1b64-39c0-11e5-9fa8-080027b8274k', NOW(), NOW(), NOW(), null, 2,800,0,2,11,null, 100000, 100000, 10000, null, 'ACCOUNT_QUOTA');