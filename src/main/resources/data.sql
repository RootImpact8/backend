-- 🌱 작물 데이터 입력 (ID 자동 증가)
INSERT INTO crop (name) VALUES ('딸기');
INSERT INTO crop (name) VALUES ('벼');
INSERT INTO crop (name) VALUES ('감자');
INSERT INTO crop (name) VALUES ('상추');
INSERT INTO crop (name) VALUES ('사과');
INSERT INTO crop (name) VALUES ('고추');

-- 🌱 task 테이블 데이터 입력 (ID 자동 증가)
-- 딸기 작업 (crop_id는 해당 작물의 자동 증가된 ID를 사용해야 함)
INSERT INTO task (name, crop_id, category) VALUES
('🌱 묘상 준비', (SELECT id FROM crop WHERE name = '딸기'), '준비'),
('👩‍🌾 가을 심기', (SELECT id FROM crop WHERE name = '딸기'), '준비'),
('💊 밑거름 시비', (SELECT id FROM crop WHERE name = '딸기'), '준비'),
('⚫️ 멀칭', (SELECT id FROM crop WHERE name = '딸기'), '준비'),
('👩‍🌾 정식', (SELECT id FROM crop WHERE name = '딸기'), '준비'),
('💦 초기 관수', (SELECT id FROM crop WHERE name = '딸기'), '준비'),
('🦠 1차 생육 전 방제', (SELECT id FROM crop WHERE name = '딸기'), '준비'),
('🦠 2차 생육 전 방제', (SELECT id FROM crop WHERE name = '딸기'), '준비');

-- 벼 작업
INSERT INTO task (name, crop_id, category) VALUES
('💊 밑거름 시비', (SELECT id FROM crop WHERE name = '벼'), '준비'),
('🤎 논갈이', (SELECT id FROM crop WHERE name = '벼'), '준비'),
('🚜 써레질', (SELECT id FROM crop WHERE name = '벼'), '준비'),
('🧼 종자 소독', (SELECT id FROM crop WHERE name = '벼'), '준비');

-- 감자 작업
INSERT INTO task (name, crop_id, category) VALUES
('💊 밑거름 시비', (SELECT id FROM crop WHERE name = '감자'), '준비'),
('🥔 씨감자 준비', (SELECT id FROM crop WHERE name = '감자'), '준비'),
('👩‍🌾 파종', (SELECT id FROM crop WHERE name = '감자'), '준비');

-- 상추 작업
INSERT INTO task (name, crop_id, category) VALUES
('💊 밑거름 시비', (SELECT id FROM crop WHERE name = '상추'), '준비'),
('🌱 파종', (SELECT id FROM crop WHERE name = '상추'), '준비'),
('👩‍🌾 정식', (SELECT id FROM crop WHERE name = '상추'), '준비');

-- 사과 작업
INSERT INTO task (name, crop_id, category) VALUES
('🌳 동계 전정', (SELECT id FROM crop WHERE name = '사과'), '준비'),
('💊 밑거름 시비', (SELECT id FROM crop WHERE name = '사과'), '준비'),
('👩‍🌾 묘목 식재', (SELECT id FROM crop WHERE name = '사과'), '준비');

-- 고추 작업
INSERT INTO task (name, crop_id, category) VALUES
('🌱 묘상 준비', (SELECT id FROM crop WHERE name = '고추'), '준비'),
('🌱 묘상 관리', (SELECT id FROM crop WHERE name = '고추'), '준비'),
('💊 밑거름 시비', (SELECT id FROM crop WHERE name = '고추'), '준비');