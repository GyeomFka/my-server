[공식] https://github.com/slipp/web-application-server/tree/master

[참고] https://hiiwee.tistory.com/4?category=1136032



1) 요청에 favicon이 톰캣이 반환되는 이유는?

2) 요청이 두 번 오는 이유는 ? 두 번 요청을 하기 떄문이다.
  - datainputstream 으로 받을때는 두 번 요청 받는다
  - bufferedinputstream으로 받을때는 요청 한 번만 받는다
  - 근본적인 둘의 차이를 이해해야한다.
3) 배열의 isBlank , isEmpty

---
요구사항 1.
- index.html로 호출 시 index.html을 반환한다.
  - 

-----

html은 기본적으로 GET과 POST만 지원한다
rest 설계와 ajax로 개발하고자 한다면 GET POST PUT DELETE 까지 고려해야한다.


----

로그인 성공 후 index페이지로 가면은 로그인 false인 상태다
requesthandler 메소드를 손봐야할것같다