
-- account
INSERT INTO `user` (id, name) VALUES
                                  (1, '현소이1'),
                                  (2, '현소이2');

INSERT INTO `account` (id, email, password, role, is_active, social_type, user_id) VALUES
                          (1, 'hyunsoi@naver.com', '$2a$10$O1ebLvFJBesF3RyQHzW/B.nhjX3hF5oMHs3swmL5R8muA.L.Btc/O', 'ROLE_USER', true, 'OURS', 1),
                          (2, 'hyunsoi0103@gmail.com', NULL, 'ROLE_USER', true, 'GOOGLE', 2);
