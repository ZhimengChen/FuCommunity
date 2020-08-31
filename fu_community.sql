/*
Navicat MySQL Data Transfer

Source Server         : server_118.178.252.181
Source Server Version : 80020
Source Host           : 118.178.252.181:3306
Source Database       : fu_community

Target Server Type    : MYSQL
Target Server Version : 80020
File Encoding         : 65001

Date: 2020-08-30 22:29:11
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_admin
-- ----------------------------
DROP TABLE IF EXISTS `t_admin`;
CREATE TABLE `t_admin` (
  `admin_id` int NOT NULL AUTO_INCREMENT,
  `admin_name` varchar(255) NOT NULL,
  `salt` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for t_comment
-- ----------------------------
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `news_id` int DEFAULT NULL,
  `reply_id` int DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `pic` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `comment_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_id`),
  KEY `user_id` (`user_id`),
  KEY `news_id` (`news_id`),
  KEY `reply_id` (`reply_id`),
  CONSTRAINT `t_comment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `t_comment_ibfk_2` FOREIGN KEY (`news_id`) REFERENCES `t_news` (`news_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `t_comment_ibfk_3` FOREIGN KEY (`reply_id`) REFERENCES `t_comment` (`comment_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for t_like
-- ----------------------------
DROP TABLE IF EXISTS `t_like`;
CREATE TABLE `t_like` (
  `news_id` int DEFAULT NULL,
  `comment_id` int DEFAULT NULL,
  `user_id` int NOT NULL,
  UNIQUE KEY `news_id` (`news_id`,`comment_id`,`user_id`),
  KEY `comment_id` (`comment_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_like_ibfk_1` FOREIGN KEY (`news_id`) REFERENCES `t_news` (`news_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `t_like_ibfk_2` FOREIGN KEY (`comment_id`) REFERENCES `t_comment` (`comment_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `t_like_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message` (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `receiver_id` int NOT NULL,
  `type` int NOT NULL,
  `content` varchar(255) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `has_read` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`message_id`),
  KEY `receiver_id` (`receiver_id`),
  CONSTRAINT `t_message_ibfk_1` FOREIGN KEY (`receiver_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for t_news
-- ----------------------------
DROP TABLE IF EXISTS `t_news`;
CREATE TABLE `t_news` (
  `news_id` int NOT NULL AUTO_INCREMENT,
  `publisher_id` int DEFAULT NULL,
  `organization_id` int DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `media` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '[]',
  `publish_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `has_check` tinyint DEFAULT NULL,
  PRIMARY KEY (`news_id`),
  KEY `publisher_id` (`publisher_id`),
  KEY `organization_id` (`organization_id`),
  CONSTRAINT `t_news_ibfk_1` FOREIGN KEY (`publisher_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `t_news_ibfk_2` FOREIGN KEY (`organization_id`) REFERENCES `t_organization` (`organization_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for t_organization
-- ----------------------------
DROP TABLE IF EXISTS `t_organization`;
CREATE TABLE `t_organization` (
  `organization_id` int NOT NULL AUTO_INCREMENT,
  `organization_name` varchar(255) NOT NULL,
  `logo` varchar(255) DEFAULT NULL,
  `slogan` varchar(255) DEFAULT NULL,
  `intro` varchar(255) DEFAULT NULL,
  `founder_id` int NOT NULL,
  `contact` varchar(255) NOT NULL,
  `auditor_id` int DEFAULT NULL,
  `has_check` tinyint(1) DEFAULT NULL,
  `audit_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`organization_id`),
  UNIQUE KEY `organization_name` (`organization_name`),
  KEY `admin_id` (`founder_id`),
  KEY `auditor_id` (`auditor_id`),
  CONSTRAINT `t_organization_ibfk_1` FOREIGN KEY (`founder_id`) REFERENCES `t_user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `t_organization_ibfk_2` FOREIGN KEY (`auditor_id`) REFERENCES `t_admin` (`admin_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for t_organization_admin
-- ----------------------------
DROP TABLE IF EXISTS `t_organization_admin`;
CREATE TABLE `t_organization_admin` (
  `organization_id` int NOT NULL,
  `admin_id` int NOT NULL,
  `has_check` tinyint(1) NOT NULL DEFAULT '0',
  UNIQUE KEY `organization_id` (`organization_id`,`admin_id`,`has_check`),
  KEY `admin_id` (`admin_id`),
  CONSTRAINT `t_organization_admin_ibfk_1` FOREIGN KEY (`organization_id`) REFERENCES `t_organization` (`organization_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `t_organization_admin_ibfk_2` FOREIGN KEY (`admin_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for t_organization_member
-- ----------------------------
DROP TABLE IF EXISTS `t_organization_member`;
CREATE TABLE `t_organization_member` (
  `organization_id` int NOT NULL,
  `member_id` int NOT NULL,
  `has_check` tinyint(1) NOT NULL DEFAULT '0',
  UNIQUE KEY `organization_id` (`organization_id`,`member_id`,`has_check`),
  KEY `menber_id` (`member_id`),
  CONSTRAINT `t_organization_member_ibfk_1` FOREIGN KEY (`organization_id`) REFERENCES `t_organization` (`organization_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `t_organization_member_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `phone_no` varchar(11) NOT NULL,
  `avatar` varchar(255) NOT NULL DEFAULT 'http://www.chenzhimeng.top/fu-community/media/default_avatar.png',
  `secret_key` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `student_no` int(9) unsigned zerofill DEFAULT NULL,
  `student_name` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `student_card` varchar(255) DEFAULT NULL,
  `has_check` tinyint(1) DEFAULT NULL,
  `auditor_id` int DEFAULT NULL,
  `audit_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `phone_no` (`phone_no`),
  KEY `t_user_ibfk_1` (`auditor_id`),
  CONSTRAINT `t_user_ibfk_1` FOREIGN KEY (`auditor_id`) REFERENCES `t_admin` (`admin_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for t_user_organization_fans
-- ----------------------------
DROP TABLE IF EXISTS `t_user_organization_fans`;
CREATE TABLE `t_user_organization_fans` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `organization_id` int DEFAULT NULL,
  `fans_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`fans_id`),
  UNIQUE KEY `organization_id` (`organization_id`,`fans_id`),
  KEY `fans_id` (`fans_id`),
  CONSTRAINT `t_user_organization_fans_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `t_user_organization_fans_ibfk_2` FOREIGN KEY (`organization_id`) REFERENCES `t_organization` (`organization_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `t_user_organization_fans_ibfk_3` FOREIGN KEY (`fans_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Procedure structure for delOldMessages
-- ----------------------------
DROP PROCEDURE IF EXISTS `delOldMessages`;
DELIMITER ;;
CREATE DEFINER=`czm`@`%` PROCEDURE `delOldMessages`()
BEGIN
	#Routine body goes here...
	#删除3个月前的已读信息
  DELETE FROM t_message WHERE has_read=1 AND DATE_ADD(time,INTERVAL 3 MONTH)<CURDATE();
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for delUselessOrganizations
-- ----------------------------
DROP PROCEDURE IF EXISTS `delUselessOrganizations`;
DELIMITER ;;
CREATE DEFINER=`czm`@`%` PROCEDURE `delUselessOrganizations`()
BEGIN
	#Routine body goes here...
	#删除3个月前被拒绝创建的组织
	DELETE FROM t_organization WHERE has_check=0 AND DATE_ADD(audit_time,INTERVAL 3 MONTH)<CURDATE();
END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for getOrganizationScore
-- ----------------------------
DROP FUNCTION IF EXISTS `getOrganizationScore`;
DELIMITER ;;
CREATE DEFINER=`czm`@`%` FUNCTION `getOrganizationScore`(`id` int) RETURNS int
BEGIN
	DECLARE member_count,news_count int;

	SELECT count(*) FROM t_organization_member WHERE organization_id=id INTO member_count;
	SELECT count(*) FROM t_news WHERE organization_id=id AND publish_time > DATE_SUB(NOW(),INTERVAL 30 DAY) INTO news_count;#Routine body goes here...

	RETURN member_count+news_count*10;
END
;;
DELIMITER ;

-- ----------------------------
-- Event structure for delUselessRecords
-- ----------------------------
DROP EVENT IF EXISTS `delUselessRecords`;
DELIMITER ;;
CREATE DEFINER=`czm`@`%` EVENT `delUselessRecords` ON SCHEDULE EVERY 3 MONTH STARTS '2020-08-27 19:28:30' ON COMPLETION NOT PRESERVE ENABLE DO BEGIN
	CALL delUselessOrganizations();
	CALL delOldMessages();
END
;;
DELIMITER ;
