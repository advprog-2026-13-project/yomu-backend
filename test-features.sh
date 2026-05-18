#!/bin/bash
# Test all yomu-backend features via curl
# Prerequisites: docker compose up -d && ./gradlew bootRun

BASE="http://localhost:8080"
PASS=0
FAIL=0

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

check() {
  if [ "$1" -ge 200 ] && [ "$1" -lt 300 ]; then
    echo -e "${GREEN}PASS${NC} ($1) $2"
    PASS=$((PASS+1))
  else
    echo -e "${RED}FAIL${NC} ($1) $2"
    FAIL=$((FAIL+1))
  fi
}

echo "============================================="
echo "  yomu-backend Feature Test"
echo "============================================="

# ─── 1. AUTH ─────────────────────────────────

echo ""
echo "── Auth ──"

# Login first (user might already exist from previous run)
LOGIN=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"identifier":"testuser","password":"password123"}')
LOGIN_STATUS=$(echo "$LOGIN" | tail -1)

if [ "$LOGIN_STATUS" != "200" ]; then
  # Register if login fails
  REG=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/auth/register" \
    -H "Content-Type: application/json" \
    -d '{"username":"testuser","displayName":"Test User","email":"test@mail.com","password":"password123"}')
  REG_STATUS=$(echo "$REG" | tail -1)
  check $REG_STATUS "Register"
  
  # Login after register
  LOGIN=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"identifier":"testuser","password":"password123"}')
  check $(echo "$LOGIN" | tail -1) "Login"
else
  check $LOGIN_STATUS "Login (user already exists)"
fi

TOKEN=$(echo "$LOGIN" | sed '$d' | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo -e "${RED}Cannot continue without JWT token${NC}"
  exit 1
fi

# Me
ME=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/auth/me" \
  -H "Authorization: Bearer $TOKEN")
check $ME "GET /api/auth/me"

# ─── 2. READING (Admin) ──────────────────────
# Note: needs ADMIN role. Manually set in DB first:
#   docker compose exec db psql -U yomu -d yomu -c "UPDATE users SET role='ADMIN' WHERE username='testuser';"

echo ""
echo "── Reading (Admin, needs ADMIN role) ──"

# Create reading
READ=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/admin/readings" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title":"Java Basics","content":"Java is a programming language...","author":"admin"}')
STATUS=$(echo "$READ" | tail -1)
READING_ID=$(echo "$READ" | sed '$d' | grep -o '"readingId":"[^"]*"' | cut -d'"' -f4)
check $STATUS "POST /api/admin/readings"

if [ -n "$READING_ID" ]; then
  # Get reading
  GET_R=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/admin/readings/$READING_ID" \
    -H "Authorization: Bearer $TOKEN")
  check $GET_R "GET /api/admin/readings/$READING_ID"

  # Add question
  Q=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/admin/readings/$READING_ID/questions" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d '{"questionText":"What is Java?","options":["Coffee","Language","Island"],"correctAnswer":"Language"}')
  STATUS=$(echo "$Q" | tail -1)
  QUESTION_ID=$(echo "$Q" | sed '$d' | grep -o '"questionId":"[^"]*"' | cut -d'"' -f4)
  check $STATUS "POST /api/admin/readings/$READING_ID/questions"

  # List all readings
  GET_ALL=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/admin/readings" \
    -H "Authorization: Bearer $TOKEN")
  check $GET_ALL "GET /api/admin/readings"
fi

# ─── 3. READING (Student) ────────────────────

echo ""
echo "── Reading (Student) ──"

# Get available readings
AVAIL=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/student/readings" \
  -H "Authorization: Bearer $TOKEN")
check $AVAIL "GET /api/student/readings"

if [ -n "$READING_ID" ]; then
  # Get specific reading
  GET_SR=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/student/readings/$READING_ID" \
    -H "Authorization: Bearer $TOKEN")
  check $GET_SR "GET /api/student/readings/$READING_ID"

  # Get questions
  GET_Q=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/student/readings/$READING_ID/questions" \
    -H "Authorization: Bearer $TOKEN")
  check $GET_Q "GET /api/student/readings/$READING_ID/questions"

  # Submit quiz
  if [ -n "$QUESTION_ID" ]; then
    SUBMIT=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/api/student/readings/$READING_ID/submit" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d "{\"answers\":[{\"questionId\":\"$QUESTION_ID\",\"selectedAnswer\":\"Language\"}]}")
    check $SUBMIT "POST /api/student/readings/$READING_ID/submit"
  fi
fi

# ─── 4. FORUM ────────────────────────────────

echo ""
echo "── Forum ──"

if [ -n "$READING_ID" ]; then
  # Post comment
  COMMENT=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/forums/$READING_ID/comments" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d '{"content":"Great reading!"}')
  STATUS=$(echo "$COMMENT" | tail -1)
  COMMENT_ID=$(echo "$COMMENT" | sed '$d' | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
  check $STATUS "POST /api/forums/$READING_ID/comments"

  # Get comments (needs auth due to SecurityConfig)
  GET_C=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/forums/$READING_ID/comments" \
    -H "Authorization: Bearer $TOKEN")
  check $GET_C "GET /api/forums/$READING_ID/comments"

  if [ -n "$COMMENT_ID" ]; then
    # Reply
    REPLY=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/api/forums/comments/$COMMENT_ID/replies" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d '{"content":"I agree!"}')
    check $REPLY "POST /api/forums/comments/$COMMENT_ID/replies"

    # React
    REACT=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/api/forums/comments/$COMMENT_ID/reactions" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d '{"type":"UPVOTE"}')
    check $REACT "POST /api/forums/comments/$COMMENT_ID/reactions (UPVOTE)"

    # Edit
    EDIT=$(curl -s -o /dev/null -w "%{http_code}" -X PUT "$BASE/api/forums/comments/$COMMENT_ID" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d '{"newContent":"Great reading, updated!"}')
    check $EDIT "PUT /api/forums/comments/$COMMENT_ID"

    # Delete
    DEL_C=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BASE/api/forums/comments/$COMMENT_ID" \
      -H "Authorization: Bearer $TOKEN")
    check $DEL_C "DELETE /api/forums/comments/$COMMENT_ID"
  fi
fi

# ─── 5. ACHIEVEMENTS ─────────────────────────

echo ""
echo "── Achievements ──"

# List achievements
ACH=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/achievements" \
  -H "Authorization: Bearer $TOKEN")
check $ACH "GET /api/achievements"

# List daily missions
DM=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/achievements/daily-missions" \
  -H "Authorization: Bearer $TOKEN")
check $DM "GET /api/achievements/daily-missions"

# Create achievement (admin)
CREATE_ACH=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/admin/achievements" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"Read 5 articles","description":"Complete 5 readings","type":"READING_COMPLETED","milestone":5}')
STATUS=$(echo "$CREATE_ACH" | tail -1)
ACH_ID=$(echo "$CREATE_ACH" | sed '$d' | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
check $STATUS "POST /api/admin/achievements"

if [ -n "$ACH_ID" ]; then
  # User progress
  USER_ID=$(curl -s "$BASE/api/auth/me" -H "Authorization: Bearer $TOKEN" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
  if [ -n "$USER_ID" ]; then
    PROG=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/achievements/users/$USER_ID/progress" \
      -H "Authorization: Bearer $TOKEN")
    check $PROG "GET /api/achievements/users/$USER_ID/progress"

    COMPLETED=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/achievements/users/$USER_ID/completed" \
      -H "Authorization: Bearer $TOKEN")
    check $COMPLETED "GET /api/achievements/users/$USER_ID/completed"
  fi

  # Delete achievement
  DEL_ACH=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BASE/api/admin/achievements/$ACH_ID" \
    -H "Authorization: Bearer $TOKEN")
  check $DEL_ACH "DELETE /api/admin/achievements/$ACH_ID"
fi

# Create daily mission
MISSION=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/admin/achievements/daily-missions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"Daily reader","description":"Read 1 article today","targetType":"READING_COMPLETED","milestone":1}')
STATUS=$(echo "$MISSION" | tail -1)
MISSION_ID=$(echo "$MISSION" | sed '$d' | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
check $STATUS "POST /api/admin/achievements/daily-missions"

if [ -n "$MISSION_ID" ]; then
  DEL_M=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BASE/api/admin/achievements/daily-missions/$MISSION_ID" \
    -H "Authorization: Bearer $TOKEN")
  check $DEL_M "DELETE /api/admin/achievements/daily-missions/$MISSION_ID"
fi

# ─── SUMMARY ─────────────────────────────────

echo ""
echo "============================================="
echo "  Results: ${GREEN}$PASS passed${NC}, ${RED}$FAIL failed${NC}"
echo "============================================="
