<div align="center">
<h1>온라인 쇼핑몰 상품 카테고리 구현</h1>
</div>

## 1. 개발 환경

    JDK : 11
    Database : H2

## 2.SWAGGER

    - http://localhost:8080/swagger-ui/

## 3.설치

    - $ git clone https://github.com/hwkim3366/musinsa-project.git


## 4.빌드

    - $ ./gradlew bootJar


## 5.실행

    - $ java -jar ./build/libs/musinsa-category-0.0.1-SNAPSHOT.jar


## 6.기타 

    - 프로그램 전반적으로 상위 카테고리 ID (PARENT_ID) 가 null 이거나 음수이면 최상위 카테고리로 취급하여 처리합니다.

