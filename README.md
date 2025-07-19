WebSocket, Kotlin Coroutine, Google Cloud Platform(GCP)을 기반으로 구현한 실시간 채팅 및 AI 연동 서버 프로젝트입니다. 워크로드 특성에 맞는 인프라를 선택하고, 안정적인 서비스 운영을 위한 CI/CD 및 모니터링 방안까지 고려하여 설계했습니다.

# Architecture
<img width="580" height="600" alt="archi" src="https://github.com/user-attachments/assets/9689e7c6-aaf5-4c28-8104-ae0421988004" />

# Tech Stack
- Backend :	Kotlin, Coroutine, Ktor (or Spring WebFlux), WebSocket
- Database & Cache : MongoDB Atlas, Redis (Google Cloud MemoryStore)
- Infrastructure : Google Cloud Platform (GCP)
  - Compute :	Compute Engine(Container-Optimized OS), Cloud Run
  - Networking : Cloud Load Balancer (ALB)
  - DevOps : Docker, Cloud Build
 
# Key Feature
- WebSocket 기반 실시간 채팅: Kotlin Coroutine을 활용한 비동기 방식으로 안정적인 실시간 양방향 통신을 구현했습니다.
- 분산 환경에서의 확장성: Redis의 Pub/Sub 기능을 이용해 여러 서버 인스턴스 간 메시지를 브로드캐스팅하고, 세션을 공유하여 수평적 확장이 가능한 구조를 설계했습니다.
- Container-Optimized OS와 같은 환경에서는 서버 내부에 직접 Nginx를 설치하여 SSL 인증서를 관리하기 어렵습니다. 이 문제를 해결하기 위해 Google Cloud Load Balancer를 도입하여 SSL 인증서를 적용하고 TLS Termination을 수행했습니다.
