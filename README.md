# WHERE2PARK 어디댈카
> 실시간 주차장 공간 정보 제공 내비게이션 개발

평소 내비게이션에는 목적지에 대한 정보만 제공되고, 차를 주차할 공간이 얼마나 남아있는 지는 제공하지 않는데, 
차주의 경우 주차할 공간이 없다면 목적지에 도착하더라도 근처에 주차할 공간을 찾느라 많은 시간을 허비하고는 합니다. 
다가올 스마트 시티에 걸맞는, 주차대수 정보까지 제공공하는 스마트 내비게이션을 개발해보았습니다.

## 개발환경 및 개발언어
> Android Studio, Kakao Navigation Open API, ~~Google Maps Open API~~, Arduino Uno, Kakao Map API

## 실행 화면

<img src="https://user-images.githubusercontent.com/33089715/120924168-4ae5ab80-c70d-11eb-88a2-81363b702735.jpeg" width = "250"> <img src="https://user-images.githubusercontent.com/33089715/120924091-efb3b900-c70c-11eb-9ec0-9b5ebfdf99c3.jpeg" width = "250"> <img src="https://user-images.githubusercontent.com/33089715/120924094-f2161300-c70c-11eb-8453-ce5c4b3f5968.jpeg" width = "250">

<img src="https://user-images.githubusercontent.com/33089715/120924096-f3474000-c70c-11eb-9dc4-44ae34008db0.jpeg" width = "250"> <img src="https://user-images.githubusercontent.com/33089715/120924099-f3dfd680-c70c-11eb-8c02-b36e714d3c07.jpeg" width = "250"> <img src="https://user-images.githubusercontent.com/33089715/120924100-f4786d00-c70c-11eb-937d-abaa528e20c7.jpeg" width = "250">

<img src="https://user-images.githubusercontent.com/33089715/120924101-f5110380-c70c-11eb-8c7f-98e138a0917f.jpeg" width = "250">

## 수정사항
21/6/4 Google Maps Open API를 Kakao Map API 로 수정 완료
+ Google Maps Open API를 지원하지 않기 때문에 사용 불가, 따라서 Kakao Map API로 수정

21/6/5 서버 값 불러 올 때 에러 수정 완료

21/6/6 Volley에서 Retrofit으로 수정 완료
