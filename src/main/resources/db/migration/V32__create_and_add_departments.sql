CREATE TABLE `koin`.`departments`
(
    `dept_num`        VARCHAR(5)   NOT NULL,
    `name`            VARCHAR(45)  NOT NULL,
    `curriculum_link` VARCHAR(255) NOT NULL,
    `is_deleted`      TINYINT(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`dept_num`),
    UNIQUE INDEX `dept_name_UNIQUE` (`name` ASC)
) ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_bin;

INSERT INTO `koin`.`departments` (`dept_num`, `name`, `curriculum_link`)
VALUES ('20', '기계공학부', 'https://cms3.koreatech.ac.kr/me/795/subview.do');
INSERT INTO `koin`.`departments` (`dept_num`, `name`, `curriculum_link`)
VALUES ('40', '메카트로닉스공학부', 'https://www.koreatech.ac.kr/kor/CMS/UnivOrganMgr/subMain.do?mCode=MN076');
INSERT INTO `koin`.`departments` (`dept_num`, `name`, `curriculum_link`)
VALUES ('61', '전기전자통신공학부', 'https://cms3.koreatech.ac.kr/ite/842/subview.do');
INSERT INTO `koin`.`departments` (`dept_num`, `name`, `curriculum_link`)
VALUES ('36', '컴퓨터공학부', 'https://cse.koreatech.ac.kr/page_izgw21');
INSERT INTO `koin`.`departments` (`dept_num`, `name`, `curriculum_link`)
VALUES ('51', '디자인공학부', 'https://cms3.koreatech.ac.kr/ide/1047/subview.do');
INSERT INTO `koin`.`departments` (`dept_num`, `name`, `curriculum_link`)
VALUES ('72', '건축공학부', 'https://cms3.koreatech.ac.kr/arch/1083/subview.do');
INSERT INTO `koin`.`departments` (`dept_num`, `name`, `curriculum_link`)
VALUES ('74', '에너지신소재화학공학부', 'https://cms3.koreatech.ac.kr/ace/992/subview.do');
INSERT INTO `koin`.`departments` (`dept_num`, `name`, `curriculum_link`)
VALUES ('80', '산업경영학부', 'https://cms3.koreatech.ac.kr/sim/1167/subview.do');
INSERT INTO `koin`.`departments` (`dept_num`, `name`, `curriculum_link`)
VALUES ('85', '고용서비스정책학과', 'https://www.koreatech.ac.kr/kor/CMS/UnivOrganMgr/subMain.do?mCode=MN451');
