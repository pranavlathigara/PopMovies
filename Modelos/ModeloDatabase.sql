-- MySQL Script generated by MySQL Workbench
-- 12/17/16 18:15:03
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`Usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Usuario` (
  `idUsuario` INT NOT NULL,
  `nome` VARCHAR(50) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `senha` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idUsuario`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Profile`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Profile` (
  `idProfile` INT NOT NULL,
  `descricao` VARCHAR(500) NULL,
  `Usuario_idUsuario` INT NOT NULL,
  `fotoPath` VARCHAR(200) NULL,
  PRIMARY KEY (`idProfile`),
  INDEX `fk_Profile_Usuario_idx` (`Usuario_idUsuario` ASC),
  CONSTRAINT `fk_Profile_Usuario`
    FOREIGN KEY (`Usuario_idUsuario`)
    REFERENCES `mydb`.`Usuario` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Filme`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Filme` (
  `idFilme` INT NOT NULL,
  `titulo` VARCHAR(100) NOT NULL,
  `favorito` INT NULL,
  `numVotos` DECIMAL NULL,
  `posterPath` VARCHAR(200) NULL,
  `dataSalvamento` DATETIME NOT NULL,
  `Profile_idProfile` INT NOT NULL,
  `idServer` INT NOT NULL,
  PRIMARY KEY (`idFilme`, `Profile_idProfile`),
  INDEX `fk_Filme_Profile1_idx` (`Profile_idProfile` ASC),
  UNIQUE INDEX `idServer_UNIQUE` (`idServer` ASC),
  CONSTRAINT `fk_Filme_Profile1`
    FOREIGN KEY (`Profile_idProfile`)
    REFERENCES `mydb`.`Profile` (`idProfile`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;