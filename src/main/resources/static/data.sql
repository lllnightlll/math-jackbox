-- Начальные вопросы викторины (перезаписываются при каждом старте приложения).
DELETE FROM question_option;
DELETE FROM questions;

-- 2+2: варианты 2, 3, 4, 5 — правильный ответ 4 (индекс 2)
INSERT INTO questions (text, correct_option_index) VALUES ('2+2', 2);
SET @q1 = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
    (@q1, '2'),
    (@q1, '3'),
    (@q1, '4'),
    (@q1, '5');

-- 3+3: варианты 3, 4, 5, 6 — правильный ответ 6 (индекс 3)
INSERT INTO questions (text, correct_option_index) VALUES ('3+3', 3);
SET @q2 = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
    (@q2, '3'),
    (@q2, '4'),
    (@q2, '5'),
    (@q2, '6');
