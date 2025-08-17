# 추가 커맨드

## 에러 핸들링 공통화 및 국제화 처리

### backend

```
기존 Controller 에서 error handling 하고 
있는 부분을 GlobalExceptionHandler 쪽으로 
하게 바꿔줘

추가로 에러 응답도 http status 와 오류 
메시지를 담도록 별도 클래스로 생성해

기본적으로 메시지는 ExceptionCode 에 따라 
분기하게 하고, 코드에 따른 메시지 국제화 
분기를 고려해서 만들어야 해
```