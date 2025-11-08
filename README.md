# MockEnventPublisher

Projeto Spring Boot para publicação de eventos mockados em Kafka, com validação de payloads e integração com Avro e Schema Registry.

## Funcionalidades

- Recebe eventos via API REST (`/mock-envent`)
- Valida dados do evento usando validadores customizados
- Publica eventos em tópicos Kafka
- Suporte a payloads dinâmicos (`Map<String, Object>`)
- Integração com Avro para serialização
- Configuração de Schema Registry

## Estrutura

- `controller/`: Endpoints REST
- `dto/`: Data Transfer Objects
- `publisher/`: Produtor Kafka
- `usecase/`: Lógica de publicação
- `validator/`: Validadores de eventos
- `config/`: Configurações Kafka e Schema Registry
