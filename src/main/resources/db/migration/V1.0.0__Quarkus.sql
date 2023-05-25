CREATE TABLE Movie
(
  id   BIGINT,
  title VARCHAR(255),
  overview VARCHAR(255)
);

CREATE sequence Movie_SEQ start with 150 increment by 50;

INSERT INTO movie(id, title, overview) VALUES (50, 'The Hobbit: An Unexpected Journey', 'Bilbo Baggins, a hobbit enjoying his quiet life, is swept into an epic quest by Gandalf the Grey and thirteen dwarves who seek to reclaim their mountain home from Smaug, the dragon.');
INSERT INTO movie(id, title, overview) VALUES (100, 'The Hobbit: The Desolation of Smaug', 'The Dwarves, Bilbo and Gandalf have successfully escaped the Misty Mountains, and Bilbo has gained the One Ring. They all continue their journey to get their gold back from the Dragon, Smaug.');