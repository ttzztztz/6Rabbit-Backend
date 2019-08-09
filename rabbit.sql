-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- 主机： db
-- 生成日期： 2019-08-09 03:11:03
-- 服务器版本： 5.7.26
-- PHP 版本： 7.2.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 数据库： `rabbit`
--

-- --------------------------------------------------------

--
-- 表的结构 `attach`
--

CREATE TABLE `attach` (
  `aid` int(10) UNSIGNED NOT NULL,
  `tid` int(10) UNSIGNED DEFAULT NULL,
  `uid` int(10) UNSIGNED NOT NULL,
  `fileSize` int(10) UNSIGNED NOT NULL,
  `downloads` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `fileName` mediumtext NOT NULL,
  `originalName` varchar(256) NOT NULL,
  `creditsType` tinyint(4) NOT NULL DEFAULT '0',
  `credits` int(11) NOT NULL DEFAULT '0',
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `attach_pay_log`
--

CREATE TABLE `attach_pay_log` (
  `did` int(10) UNSIGNED NOT NULL,
  `aid` int(10) UNSIGNED NOT NULL,
  `uid` int(10) UNSIGNED NOT NULL,
  `creditsType` tinyint(4) NOT NULL,
  `credits` int(11) NOT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `credits_log`
--

CREATE TABLE `credits_log` (
  `cid` int(11) UNSIGNED NOT NULL,
  `uid` int(11) UNSIGNED NOT NULL,
  `status` tinyint(4) UNSIGNED NOT NULL,
  `type` char(12) NOT NULL,
  `description` text NOT NULL,
  `creditsType` tinyint(4) NOT NULL,
  `credits` int(11) NOT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `forum`
--

CREATE TABLE `forum` (
  `fid` int(11) UNSIGNED NOT NULL,
  `name` char(36) NOT NULL,
  `description` text NOT NULL,
  `threads` int(11) UNSIGNED NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `notification`
--

CREATE TABLE `notification` (
  `nid` int(11) UNSIGNED NOT NULL,
  `fromUid` int(11) UNSIGNED NOT NULL,
  `toUid` int(11) UNSIGNED NOT NULL,
  `content` text NOT NULL,
  `link` text NOT NULL,
  `isRead` tinyint(1) NOT NULL DEFAULT '0',
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `oauth`
--

CREATE TABLE `oauth` (
  `oid` int(10) UNSIGNED NOT NULL,
  `uid` int(10) UNSIGNED NOT NULL,
  `platform` char(36) NOT NULL DEFAULT '',
  `openid` char(48) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `post`
--

CREATE TABLE `post` (
  `pid` int(11) UNSIGNED NOT NULL,
  `uid` int(11) UNSIGNED NOT NULL,
  `tid` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `quotepid` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `isFirst` tinyint(1) NOT NULL DEFAULT '0',
  `message` mediumtext NOT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `thread`
--

CREATE TABLE `thread` (
  `tid` int(11) UNSIGNED NOT NULL,
  `fid` int(11) UNSIGNED NOT NULL,
  `uid` int(11) UNSIGNED NOT NULL,
  `subject` tinytext NOT NULL,
  `posts` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `isTop` tinyint(1) NOT NULL DEFAULT '0',
  `isClosed` tinyint(1) NOT NULL DEFAULT '0',
  `digest` tinyint(1) NOT NULL DEFAULT '0',
  `lastuid` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `firstpid` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `lastpid` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `creditsType` tinyint(4) NOT NULL DEFAULT '0',
  `credits` int(11) NOT NULL DEFAULT '0',
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `replyDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `thread_pay_log`
--

CREATE TABLE `thread_pay_log` (
  `bid` int(10) UNSIGNED NOT NULL,
  `tid` int(10) UNSIGNED NOT NULL,
  `uid` int(10) UNSIGNED NOT NULL,
  `creditsType` tinyint(4) NOT NULL,
  `credits` int(11) NOT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `user`
--

CREATE TABLE `user` (
  `uid` int(10) UNSIGNED NOT NULL,
  `username` char(64) NOT NULL,
  `realname` char(64) NOT NULL DEFAULT '',
  `gid` int(11) NOT NULL DEFAULT '2',
  `credits` int(11) NOT NULL DEFAULT '0',
  `golds` int(11) NOT NULL DEFAULT '0',
  `rmbs` int(11) NOT NULL DEFAULT '0',
  `password` char(64) NOT NULL,
  `salt` char(64) NOT NULL,
  `gender` int(1) NOT NULL DEFAULT '0',
  `email` char(64) NOT NULL,
  `mobile` char(32) NOT NULL DEFAULT '',
  `qq` char(32) NOT NULL DEFAULT '',
  `wechat` char(64) NOT NULL DEFAULT '',
  `signature` char(128) NOT NULL DEFAULT '',
  `createDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `loginDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `user_group`
--

CREATE TABLE `user_group` (
  `gid` int(11) UNSIGNED NOT NULL,
  `name` char(32) NOT NULL,
  `isAdmin` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转储表的索引
--

--
-- 表的索引 `attach`
--
ALTER TABLE `attach`
  ADD PRIMARY KEY (`aid`),
  ADD KEY `tid` (`tid`),
  ADD KEY `uid` (`uid`);

--
-- 表的索引 `attach_pay_log`
--
ALTER TABLE `attach_pay_log`
  ADD PRIMARY KEY (`did`),
  ADD KEY `uid` (`uid`),
  ADD KEY `aid` (`aid`) USING BTREE,
  ADD KEY `aid_2` (`aid`,`uid`) USING BTREE;

--
-- 表的索引 `credits_log`
--
ALTER TABLE `credits_log`
  ADD PRIMARY KEY (`cid`),
  ADD KEY `uid` (`uid`),
  ADD KEY `type` (`type`);

--
-- 表的索引 `forum`
--
ALTER TABLE `forum`
  ADD PRIMARY KEY (`fid`),
  ADD KEY `name` (`name`);

--
-- 表的索引 `notification`
--
ALTER TABLE `notification`
  ADD PRIMARY KEY (`nid`),
  ADD KEY `User` (`fromUid`,`toUid`) USING BTREE;

--
-- 表的索引 `oauth`
--
ALTER TABLE `oauth`
  ADD PRIMARY KEY (`oid`),
  ADD KEY `uid` (`uid`,`platform`),
  ADD KEY `uid_2` (`uid`);

--
-- 表的索引 `post`
--
ALTER TABLE `post`
  ADD PRIMARY KEY (`pid`),
  ADD KEY `tid` (`tid`),
  ADD KEY `uid` (`uid`),
  ADD KEY `quotepid` (`quotepid`);

--
-- 表的索引 `thread`
--
ALTER TABLE `thread`
  ADD PRIMARY KEY (`tid`),
  ADD KEY `fid` (`fid`),
  ADD KEY `uid` (`uid`),
  ADD KEY `fid_2` (`fid`,`lastpid`);

--
-- 表的索引 `thread_pay_log`
--
ALTER TABLE `thread_pay_log`
  ADD PRIMARY KEY (`bid`),
  ADD KEY `tid` (`tid`),
  ADD KEY `uid` (`uid`),
  ADD KEY `tid_2` (`tid`,`uid`);

--
-- 表的索引 `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`uid`),
  ADD UNIQUE KEY `uid` (`uid`),
  ADD KEY `username` (`username`);

--
-- 表的索引 `user_group`
--
ALTER TABLE `user_group`
  ADD PRIMARY KEY (`gid`),
  ADD UNIQUE KEY `gid` (`gid`);

--
-- 在导出的表使用AUTO_INCREMENT
--

--
-- 使用表AUTO_INCREMENT `attach`
--
ALTER TABLE `attach`
  MODIFY `aid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `attach_pay_log`
--
ALTER TABLE `attach_pay_log`
  MODIFY `did` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `credits_log`
--
ALTER TABLE `credits_log`
  MODIFY `cid` int(11) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `forum`
--
ALTER TABLE `forum`
  MODIFY `fid` int(11) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `notification`
--
ALTER TABLE `notification`
  MODIFY `nid` int(11) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `oauth`
--
ALTER TABLE `oauth`
  MODIFY `oid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `post`
--
ALTER TABLE `post`
  MODIFY `pid` int(11) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `thread`
--
ALTER TABLE `thread`
  MODIFY `tid` int(11) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `thread_pay_log`
--
ALTER TABLE `thread_pay_log`
  MODIFY `bid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `user`
--
ALTER TABLE `user`
  MODIFY `uid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `user_group`
--
ALTER TABLE `user_group`
  MODIFY `gid` int(11) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- 限制导出的表
--

--
-- 限制表 `attach`
--
ALTER TABLE `attach`
  ADD CONSTRAINT `DeleteUserAttach` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- 限制表 `attach_pay_log`
--
ALTER TABLE `attach_pay_log`
  ADD CONSTRAINT `DeleteAttachPay` FOREIGN KEY (`aid`) REFERENCES `attach` (`aid`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `DeleteUserAttachPay` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- 限制表 `credits_log`
--
ALTER TABLE `credits_log`
  ADD CONSTRAINT `DeleteUserCredits` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- 限制表 `oauth`
--
ALTER TABLE `oauth`
  ADD CONSTRAINT `DeleteUserOauth` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- 限制表 `post`
--
ALTER TABLE `post`
  ADD CONSTRAINT `DeleteThread` FOREIGN KEY (`tid`) REFERENCES `thread` (`tid`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `DeleteUserPost` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- 限制表 `thread`
--
ALTER TABLE `thread`
  ADD CONSTRAINT `DeleteUserThread` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `Forum` FOREIGN KEY (`fid`) REFERENCES `forum` (`fid`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- 限制表 `thread_pay_log`
--
ALTER TABLE `thread_pay_log`
  ADD CONSTRAINT `DeleteThreadPay` FOREIGN KEY (`tid`) REFERENCES `thread` (`tid`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `DeleteUserPay` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
