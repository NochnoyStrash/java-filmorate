# java-filmorate
Template repository for Filmorate project.
![тут должна быть схема будущей таблицы](/image/QuickDBD-export.png)
База данных состоит из таблиц с описнием юзера, фильма, лайка, статуса, дружбы, жанра и райтинга.
Для определения лайка используется  ид фильма и ид юзера, таким образом может быть только один лайк. 
Для списка друзей используется ид пользователся в качестве ключа для определенного пользователя, 
а в значении внешний ключ на ид другого пользователя. 
Дополнительный ключ статус  нужен для разделения подтвержденных или не подтвержденных заявок.

//Необходимо добавить таблицу для связи film_id и genre_id для 3NF формата

Основные команды:
```
SELECT user_id
FROM user_frends
WHERE user_friends_id = 1 AND
status_id = 1;
```
```
SELECT count(user_id)
FROM likes_film
WHERE film_id = 1;
```
```
SELECT name
FROM films
WHERE name_genre = 'Комедия';
```

//Было бы здорово увидеть примеры из ТЗ

//В целом, хорошая схема, понятные название таблиц и полей, с описанием и примерами code_style соответствует рекомендации yandex.practikum
