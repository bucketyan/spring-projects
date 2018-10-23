USE oauth2;
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(255) DEFAULT NULL COMMENT 'username',
  `password` varchar(255) DEFAULT NULL COMMENT 'password',
  `UPDATE_TIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) DEFAULT NULL COMMENT 'name',
  `UPDATE_TIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='角色表';

CREATE TABLE IF NOT EXISTS `sys_role_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` varchar(255) DEFAULT NULL COMMENT 'userId',
  `role_id` varchar(255) DEFAULT NULL COMMENT 'roleId',
  `UPDATE_TIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用户角色对应表';

CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) DEFAULT NULL COMMENT 'name',
  `description` varchar(255) DEFAULT NULL COMMENT 'description',
  `url` varchar(255) DEFAULT NULL COMMENT 'url通配符为两颗星，如/xxx下的所有url写成/xxx/**',
  `UPDATE_TIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='权限表';

CREATE TABLE IF NOT EXISTS `sys_permission_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `role_id` varchar(255) DEFAULT NULL COMMENT 'role_id',
  `permission_id` varchar(255) DEFAULT NULL COMMENT 'permission_id',
  `UPDATE_TIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='角色权限表';

insert into sys_user (id,username, password) values (1,'admin', 'admin');
insert into sys_user (id,username, password) values (2,'user', 'user');

insert into sys_role(id,name) values(1,'ROLE_ADMIN');
insert into sys_role(id,name) values(2,'ROLE_USER');

insert into sys_role_user(user_id,role_id) values(1,1);
insert into sys_role_user(user_id,role_id) values(2,2);


INSERT INTO sys_permission(id,name,description,url) VALUES ('1', 'ROLE_TEST', 'demo服务', '/demo/test');
INSERT INTO sys_permission(id,name,description,url) VALUES ('2', 'ROLE_TEST2', 'demo服务', '/demo/test2');



INSERT INTO sys_permission_role(id,role_id,permission_id) VALUES ('1', '1', '1');
INSERT INTO sys_permission_role(id,role_id,permission_id) VALUES ('2', '1', '2');
INSERT INTO sys_permission_role(id,role_id,permission_id) VALUES ('3', '2', '1');


