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


