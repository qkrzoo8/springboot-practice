# 백엔드 프레임워크 실습 과제

수업에서 제공된 Spring Boot 예제를 바탕으로 상품 관리 API를 구현했습니다.
기존 코드에 상품 복제, 가격 수정, 전체 삭제 기능 등을 추가하고 응답 형식을 통일했습니다.

참고한 코드: https://github.com/JBNU-KSL-25-02/springboot-tutorial

## 실행 환경 및 방법

- Java 21
- Spring Boot 3.5.7
- Gradle
- IntelliJ IDEA
- Postman

IntelliJ에서 프로젝트를 열고 Gradle 동기화가 끝나면
`Wsd2025Application`을 실행합니다.

기본 주소는 `http://localhost:8080`입니다.
포트를 변경했다면 변경한 포트로 접속해야 합니다.

상품 정보는 HashMap에 저장하기 때문에 서버를 다시 실행하면 사라집니다.
별도의 데이터베이스는 사용하지 않았습니다.

## 구현한 API

과제 코드는 `ItemController3.java`에 작성했습니다.
v1, v2는 수업 예제로 남겨두었고, 테스트는 v3 주소로 진행했습니다.

| 메소드 | 주소 | 기능 |
|---|---|---|
| POST | `/api/v3/items` | 상품 등록 |
| POST | `/api/v3/items/{id}/copy` | 상품 복제 |
| GET | `/api/v3/items` | 전체 상품 조회 |
| GET | `/api/v3/items/{id}` | 상품 한 개 조회 |
| PUT | `/api/v3/items/{id}` | 상품 이름과 가격 수정 |
| PUT | `/api/v3/items/{id}/price` | 상품 가격만 수정 |
| DELETE | `/api/v3/items/{id}` | 상품 한 개 삭제 |
| DELETE | `/api/v3/items/all` | 전체 상품 삭제 |

POST, GET, PUT, DELETE를 각각 2개씩 총 8개 구현했습니다.

상품 등록과 정보 수정에서는 이름과 가격을 전달합니다.

```json
{
  "name": "노트",
  "price": 3000
}
```

가격만 수정할 때는 price만 전달합니다.

```json
{
  "price": 5000
}
```

상품을 복제하면 새로운 ID가 부여되고 이름 뒤에 `(복사본)`이 붙습니다.
전체 삭제를 하면 삭제한 상품 개수를 응답으로 보여줍니다.

## 응답 형식

과제 공지에 있는 예시를 참고해서 `ApiResponse` 클래스를 만들었습니다.
성공과 오류 응답 모두 `status`와 `data`를 사용하도록 작성했습니다.

성공 응답 예시:

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "name": "노트",
    "price": 3000
  }
}
```

오류 응답 예시:

```json
{
  "status": "error",
  "data": "해당 상품이 없습니다."
}
```

이름이나 가격이 잘못된 경우에는 400을 반환하고,
없는 상품을 조회하거나 수정·삭제하려고 하면 404를 반환합니다.

잘못된 JSON이나 숫자가 아닌 상품 ID가 들어오는 경우는
`ApiExceptionHandler`에서 처리하도록 했습니다.

## 사용한 응답 코드

| 코드 | 사용한 경우 |
|---|---|
| 200 | 조회, 수정, 삭제 성공 |
| 201 | 상품 등록, 복제 성공 |
| 400 | 잘못된 입력값 |
| 404 | 대상 상품이 없을 때 |
| 500 | 서버 오류 상황 테스트 |
| 503 | 서비스 점검 상황 테스트 |

500과 503은 실제 서버 장애를 일으키는 대신,
쿼리 파라미터로 상황을 재현하도록 작성했습니다.

```text
/api/v3/items?simulation=error
→ 500 응답

/api/v3/items?simulation=maintenance
→ 503 응답
```

503 응답에는 `Retry-After: 60` 헤더도 추가했습니다.

## 미들웨어

수업 예제에 있는 `LoggingInterceptor`는 요청이 들어왔을 때
메소드와 주소를 출력하고, 처리가 끝나면 응답 상태 코드를 출력합니다.
`WebConfig`에서 `/api/**` 경로에 적용되어 있습니다.

실행 창에서 다음 로그가 나오는 것을 확인했습니다.

```text
[LoggingInterceptor] 요청 시작: GET /api/v3/items
[LoggingInterceptor] 요청 완료, status=200
```

추가로 `RequestLoggingFilter`를 작성했습니다.
요청이 들어온 시간과 처리가 끝난 시간의 차이를 계산해서
응답 상태 코드와 처리 시간(ms)을 출력하도록 했습니다.

## 테스트

Postman으로 API 8개를 요청하고 결과를 확인했습니다.

- 상품 등록 후 복제하기
- 전체 상품과 상품 한 개 조회하기
- 상품 이름과 가격 수정하기
- 가격만 수정하기
- 상품 한 개와 전체 상품 삭제하기
- 빈 이름으로 등록해서 400 확인하기
- 없는 상품을 조회해서 404 확인하기
- 테스트용 파라미터로 500과 503 확인하기

요청 주소, 응답 상태 코드, JSON 응답이 보이도록 화면을 캡처했습니다.
실행 및 테스트 캡처는 과제 제출 시 함께 첨부합니다.