# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM gradle:8.10-jdk17 AS builder

WORKDIR /app

# 의존성 캐시 레이어 (소스 변경 시 재다운로드 방지)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

# 소스 복사 후 빌드 (테스트 제외)
COPY src ./src
RUN gradle build -x test --no-daemon

# ── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 빌드 산출물 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 업로드 파일 저장 디렉토리 생성 (docker-compose 볼륨으로 마운트)
RUN mkdir -p /app/uploads

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
