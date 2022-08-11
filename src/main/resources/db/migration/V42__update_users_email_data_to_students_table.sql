UPDATE students, users
SET students.email = concat(users.account, '@koreatech.ac.kr')
WHERE students.user_id = users.id;