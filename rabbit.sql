-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: db
-- Generation Time: Aug 17, 2019 at 12:54 PM
-- Server version: 5.7.26
-- PHP Version: 7.2.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `rabbit`
--

-- --------------------------------------------------------

--
-- Table structure for table `attach`
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `attach_pay_log`
--

CREATE TABLE `attach_pay_log` (
  `did` int(10) UNSIGNED NOT NULL,
  `aid` int(10) UNSIGNED NOT NULL,
  `uid` int(10) UNSIGNED NOT NULL,
  `creditsType` tinyint(4) NOT NULL,
  `credits` int(11) NOT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `credits_log`
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `forum`
--

CREATE TABLE `forum` (
  `fid` int(11) UNSIGNED NOT NULL,
  `name` char(36) NOT NULL,
  `description` text NOT NULL,
  `threads` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `type` char(10) NOT NULL DEFAULT 'normal',
  `adminPost` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `notification`
--

CREATE TABLE `notification` (
  `nid` int(11) UNSIGNED NOT NULL,
  `fromUid` int(11) UNSIGNED NOT NULL,
  `toUid` int(11) UNSIGNED NOT NULL,
  `content` text NOT NULL,
  `link` text NOT NULL,
  `isRead` tinyint(1) NOT NULL DEFAULT '0',
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `oauth`
--

CREATE TABLE `oauth` (
  `oid` int(10) UNSIGNED NOT NULL,
  `uid` int(10) UNSIGNED NOT NULL,
  `platform` char(36) NOT NULL DEFAULT '',
  `openid` char(48) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `post`
--

CREATE TABLE `post` (
  `pid` int(11) UNSIGNED NOT NULL,
  `uid` int(11) UNSIGNED NOT NULL,
  `tid` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `quotepid` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `isFirst` tinyint(1) NOT NULL DEFAULT '0',
  `message` mediumtext NOT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ;

-- --------------------------------------------------------

--
-- Table structure for table `thread`
--

CREATE TABLE `thread` (
  `tid` int(11) UNSIGNED NOT NULL,
  `fid` int(11) UNSIGNED NOT NULL,
  `uid` int(11) UNSIGNED NOT NULL,
  `subject` tinytext NOT NULL,
  `posts` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `isTop` tinyint(1) NOT NULL DEFAULT '0',
  `isClosed` tinyint(1) NOT NULL DEFAULT '0',
  `diamond` tinyint(1) NOT NULL DEFAULT '0',
  `lastuid` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `firstpid` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `lastpid` int(11) UNSIGNED NOT NULL DEFAULT '0',
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `replyDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `user`
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
  `gender` int(1) NOT NULL DEFAULT '2',
  `email` char(64) NOT NULL,
  `mobile` char(32) NOT NULL DEFAULT '',
  `qq` char(32) NOT NULL DEFAULT '',
  `wechat` char(64) NOT NULL DEFAULT '',
  `signature` char(128) NOT NULL DEFAULT '',
  `createDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `loginDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `user_group`
--

CREATE TABLE `user_group` (
  `gid` int(11) UNSIGNED NOT NULL,
  `name` char(32) NOT NULL,
  `color` char(16) DEFAULT 'dodgerblue',
  `isAdmin` tinyint(1) NOT NULL DEFAULT '0',
  `canLogin` tinyint(1) NOT NULL DEFAULT '1',
  `canPost` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `attach`
--
ALTER TABLE `attach`
  ADD PRIMARY KEY (`aid`),
  ADD KEY `tid` (`tid`),
  ADD KEY `uid` (`uid`);

--
-- Indexes for table `attach_pay_log`
--
ALTER TABLE `attach_pay_log`
  ADD PRIMARY KEY (`did`),
  ADD KEY `uid` (`uid`),
  ADD KEY `aid` (`aid`) USING BTREE,
  ADD KEY `aid_2` (`aid`,`uid`) USING BTREE;

--
-- Indexes for table `credits_log`
--
ALTER TABLE `credits_log`
  ADD PRIMARY KEY (`cid`),
  ADD KEY `uid` (`uid`),
  ADD KEY `type` (`type`);

--
-- Indexes for table `forum`
--
ALTER TABLE `forum`
  ADD PRIMARY KEY (`fid`),
  ADD KEY `name` (`name`);

--
-- Indexes for table `notification`
--
ALTER TABLE `notification`
  ADD PRIMARY KEY (`nid`),
  ADD KEY `User` (`fromUid`,`toUid`) USING BTREE;

--
-- Indexes for table `oauth`
--
ALTER TABLE `oauth`
  ADD PRIMARY KEY (`oid`),
  ADD KEY `uid` (`uid`,`platform`),
  ADD KEY `uid_2` (`uid`);

--
-- Indexes for table `post`
--
ALTER TABLE `post`
  ADD PRIMARY KEY (`pid`),
  ADD KEY `tid` (`tid`),
  ADD KEY `uid` (`uid`),
  ADD KEY `quotepid` (`quotepid`);
ALTER TABLE `post` ADD FULLTEXT KEY `message` (`message`);

--
-- Indexes for table `thread`
--
ALTER TABLE `thread`
  ADD PRIMARY KEY (`tid`),
  ADD KEY `fid` (`fid`),
  ADD KEY `uid` (`uid`);
ALTER TABLE `thread` ADD FULLTEXT KEY `subject` (`subject`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`uid`),
  ADD UNIQUE KEY `uid` (`uid`),
  ADD KEY `username` (`username`);

--
-- Indexes for table `user_group`
--
ALTER TABLE `user_group`
  ADD PRIMARY KEY (`gid`),
  ADD UNIQUE KEY `gid` (`gid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `attach`
--
ALTER TABLE `attach`
  MODIFY `aid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `attach_pay_log`
--
ALTER TABLE `attach_pay_log`
  MODIFY `did` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `credits_log`
--
ALTER TABLE `credits_log`
  MODIFY `cid` int(11) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `forum`
--
ALTER TABLE `forum`
  MODIFY `fid` int(11) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notification`
--
ALTER TABLE `notification`
  MODIFY `nid` int(11) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `oauth`
--
ALTER TABLE `oauth`
  MODIFY `oid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `post`
--
ALTER TABLE `post`
  MODIFY `pid` int(11) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `thread`
--
ALTER TABLE `thread`
  MODIFY `tid` int(11) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `uid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_group`
--
ALTER TABLE `user_group`
  MODIFY `gid` int(11) UNSIGNED NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
