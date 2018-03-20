-- phpMyAdmin SQL Dump
-- version 4.1.6
-- http://www.phpmyadmin.net
--
-- Client :  localhost
-- Généré le :  Jeu 17 Novembre 2016 à 22:37
-- Version du serveur :  5.5.53-0+deb8u1-log
-- Version de PHP :  5.6.27-0+deb8u1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données :  `tp_jdbc`
--

-- --------------------------------------------------------

--
-- Structure de la table `joueur`
--

CREATE TABLE IF NOT EXISTS `joueur` (
  `pseudo` varchar(20) CHARACTER SET utf8 NOT NULL,
  `email` varchar(100) CHARACTER SET utf8 NOT NULL,
  `motDePasse` varchar(32) CHARACTER SET utf8 NOT NULL COMMENT 'codé en md5',
  `latitude` double NOT NULL COMMENT 'en degré décimal',
  `longitude` double NOT NULL COMMENT 'en degré décimal',
  `dateDerniereConnexion` datetime NOT NULL,
  PRIMARY KEY (`pseudo`),
  UNIQUE KEY `email` (`email`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Contenu de la table `joueur`
--

INSERT INTO `joueur` (`pseudo`, `email`, `motDePasse`, `latitude`, `longitude`, `dateDerniereConnexion`) VALUES
('admin', 'cri@ens2m.fr', '', 47.25097, 5.99432, '2016-11-15 10:49:47'),
('sacha', 'sacha@ens2m.org', '9ce44f88a25272b6d9cbb430ebbcfcf1', 47.26, 6.01, '2016-11-15 10:49:57'),
('aurore', 'aurore@ens2m.org', '40e33500ce6df5e1fbedd52b4e8d81c1', 47.23, 5.99, '2016-11-17 17:34:26'),
('pierre', 'pierre@ens2m.fr', 'fd96a4e3eb98ba9acae5c3abc3d3e4a9', 47.27, 5.97, '2016-11-17 16:51:48');

-- --------------------------------------------------------

--
-- Structure de la table `objet`
--

CREATE TABLE IF NOT EXISTS `objet` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'identifiant de l''objet',
  `description` varchar(200) CHARACTER SET utf8 NOT NULL COMMENT 'description contenant éventuellement des caractéristiques (type, score, force, ...)',
  `latitude` double NOT NULL COMMENT 'en degré décimal',
  `longitude` double NOT NULL COMMENT 'en degré décimal',
  `visible` tinyint(1) NOT NULL,
  `dateCreation` datetime NOT NULL,
  `proprietaire` varchar(20) CHARACTER SET latin1 COLLATE latin1_general_ci NOT NULL DEFAULT 'admin' COMMENT 'pseudo du dresseur. Si non dressé la valeur est ''admin''',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=8 ;

--
-- Contenu de la table `objet`
--

INSERT INTO `objet` (`id`, `description`, `latitude`, `longitude`, `visible`, `dateCreation`, `proprietaire`) VALUES
(1, 'Pikachu', 47.26, 6.01, 1, '2016-11-15 10:50:38', 'sacha'),
(2, 'Carapuce', 47.25, 5.98, 0, '2016-11-15 10:50:45', 'aurore'),
(3, 'Evoli', 47.23, 5.99, 0, '2016-11-15 10:51:01', 'sacha'),
(4, 'Nidoran', 47.27, 5.96, 1, '2016-11-15 10:51:11', 'admin'),
(5, 'Tiplouf', 47.24, 6.02, 1, '2016-11-15 10:51:22', 'aurore'),
(6, 'Evoli', 47.28, 5.99, 0, '2016-11-15 10:52:07', 'admin'),
(7, 'Salameche', 47.25, 5.97, 1, '2016-11-17 22:36:00', 'sacha');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
