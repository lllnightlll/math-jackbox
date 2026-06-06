DELETE FROM question_option;
DELETE FROM questions;

ALTER TABLE questions AUTO_INCREMENT = 1;

INSERT INTO questions (text, correct_option_index) VALUES (
              'Выберете верное (-ые) утверждение (-ия):\n1. При почти равных условиях - результаты одинаковые.\n2.Стэхастическое явление - случайное, результат невозможно предсказать.', 1);

SET @q1_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q1_id, '1'),
                   (@q1_id, '2'),
                   (@q1_id, 'Оба верны'),
                   (@q1_id, 'Оба неверны');

INSERT INTO questions (text, correct_option_index) VALUES ('Выберете формулу: А и В', 2);

SET @q2_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q2_id, 'А ∪ В'),
                   (@q2_id, 'А ⊂ В'),
                   (@q2_id, 'А ∩ В'),
                   (@q2_id, 'Ā');

INSERT INTO questions (text, correct_option_index) VALUES ('Выберете формулу: А или В', 0);

SET @q3_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q3_id, 'А ∪ В'),
                   (@q3_id, 'А ⊂ В'),
                   (@q3_id, 'А ∩ В'),
                   (@q3_id, 'Ā');

INSERT INTO questions (text, correct_option_index) VALUES ('Выберете формулу: А содержится в В', 1);

SET @q4_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q4_id, 'А ∪ В'),
                   (@q4_id, 'А ⊂ В'),
                   (@q4_id, 'А ∩ В'),
                   (@q4_id, 'Ā');

INSERT INTO questions (text, correct_option_index) VALUES ('Выберете формулу: не А', 3);

SET @q5_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q5_id, 'А ∪ В'),
                   (@q5_id, 'А ⊂ В'),
                   (@q5_id, 'А ∩ В'),
                   (@q5_id, 'Ā');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Верно ли утверждение:\nСобытие - список результатов - описываются высказывания, которые могут быть истины и ложны', 0);

SET @q6_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q6_id, 'да'),
                   (@q6_id, 'нет');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Верно ли утверждение:\nКомбинаторика - это метод подсчета количества комбинаций объектов с заданным условием.', 0);

SET @q7_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q7_id, 'да'),
                   (@q7_id, 'нет');

INSERT INTO questions (text, correct_option_index) VALUES ('Сколько свойств у мат. ожидания?', 2);

SET @q8_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q8_id, '7'),
                   (@q8_id, '3'),
                   (@q8_id, '5'),
                   (@q8_id, '8');

INSERT INTO questions (text, correct_option_index) VALUES ('Сколько свойств у дисперсии?', 3);

SET @q9_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q9_id, '6'),
                   (@q9_id, '9'),
                   (@q9_id, '2'),
                   (@q9_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Выберете верное определение:\n_______ - Порядок не важен.', 0);

SET @q10_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q10_id, 'Сочетания'),
                   (@q10_id, 'Размещения'),
                   (@q10_id, 'Перестановки');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Выберете верное определение:\n_______ - Порядок важен.', 1);

SET @q11_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q11_id, 'Сочетания'),
                   (@q11_id, 'Размещения'),
                   (@q11_id, 'Перестановки');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Выберете верное определение:\n_______ - Частный случай размещения при n = k.', 2);

SET @q12_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q12_id, 'Сочетания'),
                   (@q12_id, 'Размещения'),
                   (@q12_id, 'Перестановки');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Классическое определение вероятности P(A) = m/n предполагает, что:\n1) Исходы могут быть неравновозможными\n2) Испытания зависимы\n3) Все элементарные исходы равновозможны\n4) Количество испытаний стремится к бесконечности', 2);

SET @q13_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q13_id, '1'),
                   (@q13_id, '2'),
                   (@q13_id, '3'),
                   (@q13_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Сколькими способами можно выбрать 3 человека из 10 для дежурства (порядок не важен)?\n1) 720\n2) 120\n3) 1000\n4) 30', 1);

SET @q14_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q14_id, '1'),
                   (@q14_id, '2'),
                   (@q14_id, '3'),
                   (@q14_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Схема Бернулли предполагает, что испытания:\n1) Зависимы, но вероятности одинаковы\n2) Независимы, вероятности успеха одинаковы\n3) Независимы, вероятности успеха различны\n4) Зависимы, вероятности различны', 1);

SET @q15_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q15_id, '1'),
                   (@q15_id, '2'),
                   (@q15_id, '3'),
                   (@q15_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Если P(A) = 0.6, P(B) = 0.4, события независимы, то P(A∪B) равна:', 2);

SET @q16_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q16_id, '0.24'),
                   (@q16_id, '1.0'),
                   (@q16_id, '0.76'),
                   (@q16_id, '0.2');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Формула Байеса позволяет:\n1) Вычислить безусловную вероятность\n2) Переоценить вероятности гипотез после того, как событие произошло\n3) Найти дисперсию\n4) Определить независимость событий', 1);

SET @q17_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q17_id, '1'),
                   (@q17_id, '2'),
                   (@q17_id, '3'),
                   (@q17_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Дисперсия характеризует:\n1) Среднее значение\n2) Моду случайной величины\n3) Разброс случайной величины относительно её среднего\n4) Медиану', 2);

SET @q18_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q18_id, '1'),
                   (@q18_id, '2'),
                   (@q18_id, '3'),
                   (@q18_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Если к каждой случайной величине прибавить константу a, то её дисперсия:\n1) Увеличится на a\n2) Увеличится на a²\n3) Не изменится\n4) Уменьшится', 2);

SET @q19_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q19_id, '1'),
                   (@q19_id, '2'),
                   (@q19_id, '3'),
                   (@q19_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Монету подбрасывают 3 раза. Вероятность выпадения ровно двух орлов равна:', 1);

SET @q20_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q20_id, '1/8'),
                   (@q20_id, '3/8'),
                   (@q20_id, '1/2'),
                   (@q20_id, '1/4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Из колоды 36 карт вынимают одну. События A = «карта пиковой масти», B = «карта – туз». Они:', 0);

SET @q21_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q21_id, 'Независимы'),
                   (@q21_id, 'Несовместны'),
                   (@q21_id, 'Зависимы'),
                   (@q21_id, 'Противоположны');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Бросают две правильные игральные кости. Какова вероятность суммы очков, равной 7?', 0);

SET @q22_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q22_id, '1/6'),
                   (@q22_id, '1/8'),
                   (@q22_id, '1/12'),
                   (@q22_id, '1/36');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'События A и B независимы. P(A)=0.4, P(B)=0.5. Чему равно P(A∩B)?', 0);

SET @q23_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q23_id, '0.2'),
                   (@q23_id, '0.9'),
                   (@q23_id, '0.7'),
                   (@q23_id, '0');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Формула полной вероятности используется, когда:\n1) Нужно найти вероятность пересечения событий\n2) Событие может наступить только вместе с одним из несовместных «гипотез»\n3) Все события равновероятны\n4) Нужно найти условную вероятность', 1);

SET @q24_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q24_id, '1'),
                   (@q24_id, '2'),
                   (@q24_id, '3'),
                   (@q24_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'События A и B независимы. P(A)=0.3, P(B)=0.6. Чему равно P(A∪B)?', 2);

SET @q25_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q25_id, '0.18'),
                   (@q25_id, '0.9'),
                   (@q25_id, '0.72'),
                   (@q25_id, '0.12');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Верно ли утверждение:\nДля дискретной случайной величины функция распределения F(x) в точке разрыва равна нулю.', 1);

SET @q26_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q26_id, 'да'),
                   (@q26_id, 'нет');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Формула соответствует утверждению?\nВероятность одновременного наступления двух событий:\nP(AB) = P(B) * P(A|B) = P(A) * P(B|A)', 0);

SET @q27_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q27_id, 'да'),
                   (@q27_id, 'нет');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Какая формула условной вероятности?', 0);

SET @q28_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q28_id, '1'),
                   (@q28_id, '2');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Какая формула Байеса?', 1);

SET @q29_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q29_id, '1'),
                   (@q29_id, '2');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Выберете верное (-ые) утверждение (-ия):\n1) Физический смысл дисперсии - момент инерции вокруг центра тяжести.\n2) Схема Бернулли предполагает два исхода, независимые испытания, постоянную вероятность успеха', 2);

SET @q30_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q30_id, '1'),
                   (@q30_id, '2'),
                   (@q30_id, 'Оба'),
                   (@q30_id, 'Нет верного');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Верно ли определение:\nСтатическая регулярность - при однократном повторении наблюдается закономерность статических частот', 1);

SET @q31_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q31_id, 'да'),
                   (@q31_id, 'нет');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Что это?', 2);

SET @q32_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q32_id, 'Конъюнкция'),
                   (@q32_id, 'Дизъюнкция'),
                   (@q32_id, 'Инверсия');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Что это?\nА ∩ В', 0);

SET @q33_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q33_id, 'Конъюнкция'),
                   (@q33_id, 'Дизъюнкция'),
                   (@q33_id, 'Инверсия');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Что это?\nА ∪ В', 1);

SET @q34_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q34_id, 'Конъюнкция'),
                   (@q34_id, 'Дизъюнкция'),
                   (@q34_id, 'Инверсия');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Какая формула условной вероятности?', 0);

SET @q35_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q35_id, '1'),
                   (@q35_id, '2');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Что изучает теория вероятностей?\n1) Детерминированные процессы\n2) Случайные явления и их вероятностные модели\n3) Только сбор и обработку данных\n4) Исключительно предельные теоремы', 1);

SET @q36_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q36_id, '1'),
                   (@q36_id, '2'),
                   (@q36_id, '3'),
                   (@q36_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Что из перечисленного НЕ является характеристикой случайной величины?\n1) Функция распределения\n2) Детерминированный тренд\n3) Дисперсия\n4) Математическое ожидание', 1);

SET @q37_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q37_id, '1'),
                   (@q37_id, '2'),
                   (@q37_id, '3'),
                   (@q37_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Центральная предельная теорема утверждает, что сумма большого числа независимых одинаково распределённых СВ стремится по распределению к:\n1) Экспоненциальному распределению\n2) Равномерному распределению\n3) Нормальному распределению\n4) Распределению Пуассона', 2);

SET @q38_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q38_id, '1'),
                   (@q38_id, '2'),
                   (@q38_id, '3'),
                   (@q38_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Вероятность события A = 0,3, B = 0,5, A и B несовместны. Чему равна P(A ∪ B)?', 0);

SET @q39_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q39_id, '0.8'),
                   (@q39_id, '0.15'),
                   (@q39_id, '0.65'),
                   (@q39_id, '0.2');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Монету подбрасывают дважды. Какова вероятность выпадения двух орлов?', 1);

SET @q40_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q40_id, '1/2'),
                   (@q40_id, '1/4'),
                   (@q40_id, '1/3'),
                   (@q40_id, '1');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Чему равна дисперсия с (константы)?', 2);

SET @q41_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q41_id, '1'),
                   (@q41_id, '-1'),
                   (@q41_id, '0'),
                   (@q41_id, 'с');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Формула Байеса используется для:\n1) Нахождения вероятности гипотез после наблюдения результата\n2) Вычисления дисперсии\n3) Проверки независимости событий\n4) Расчёта числа сочетаний', 0);

SET @q42_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q42_id, '1'),
                   (@q42_id, '2'),
                   (@q42_id, '3'),
                   (@q42_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Распределение Пуассона обычно моделирует:\n1) Число успехов в фиксированном числе испытаний\n2) Время до первого события\n3) Число редких событий за фиксированное время\n4) Рост случайного вектора', 2);

SET @q43_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q43_id, '1'),
                   (@q43_id, '2'),
                   (@q43_id, '3'),
                   (@q43_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Чему равно число сочетаний из n по k?', 3);

SET @q44_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q44_id, '1'),
                   (@q44_id, '2'),
                   (@q44_id, '3'),
                   (@q44_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Центральная предельная теорема (ЦПТ) относится к:\n1) Сходимости суммы нормированных независимых СВ к нормальному распределению\n2) Сходимости эмпирической функции распределения\n3) Закону малых чисел\n4) Неравенству Чебышёва', 0);

SET @q45_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q45_id, '1'),
                   (@q45_id, '2'),
                   (@q45_id, '3'),
                   (@q45_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Если P(A|B) = P(A) и P(A)>0, P(B)>0, то:\n1) A и B независимы\n2) P(B|A) = P(B)\n3) P(A∩B) = P(A)P(B)\n4) Все перечисленное верно', 3);

SET @q46_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q46_id, '1'),
                   (@q46_id, '2'),
                   (@q46_id, '3'),
                   (@q46_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Для дискретной случайной величины функция распределения F(x) — это:\n1) Строго возрастающая функция\n2) Ступенчатая кусочно-постоянная функция\n3) Непрерывная функция\n4) Плотность вероятности', 1);

SET @q47_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q47_id, '1'),
                   (@q47_id, '2'),
                   (@q47_id, '3'),
                   (@q47_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Если D(X) = 4, то D(3X-2) равна:', 2);

SET @q48_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q48_id, '12'),
                   (@q48_id, '34'),
                   (@q48_id, '36'),
                   (@q48_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Для любых двух СВ X и Y:', 0);

SET @q49_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q49_id, '1'),
                   (@q49_id, '2'),
                   (@q49_id, '3'),
                   (@q49_id, '4');

INSERT INTO questions (text, correct_option_index) VALUES (
                      'Для независимых X и Y с дисперсиями 9 и 16:', 3);

SET @q50_id = LAST_INSERT_ID();
INSERT INTO question_option (question_id, option_text) VALUES
                   (@q50_id, '1'),
                   (@q50_id, '5'),
                   (@q50_id, '7'),
                   (@q50_id, '25');