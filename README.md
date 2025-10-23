# Curso REST Assured

Este projeto contém exemplos práticos e exercícios para aprender a usar o REST Assured, uma biblioteca Java para testes de APIs REST.

## 📋 Sobre o Projeto

O REST Assured é uma biblioteca Java que simplifica o teste de serviços REST. Este curso aborda desde conceitos básicos até técnicas avançadas de validação de APIs, incluindo:

- Testes de verbos HTTP (GET, POST, PUT, DELETE)
- Validação de respostas JSON e XML
- Autenticação em APIs
- Uso de Matchers do Hamcrest
- Boas práticas em testes de API

## 🛠️ Tecnologias Utilizadas

- **Java**: Linguagem de programação principal
- **REST Assured 4.0.0**: Framework para testes de API REST
- **JUnit 4.12**: Framework de testes unitários
- **Gson 2.8.7**: Biblioteca para manipulação de JSON
- **Maven**: Gerenciador de dependências e build

## 📁 Estrutura do Projeto

```
src/main/java/br/com/bferreira/rest/
├── OlaMundo.java          # Exemplo básico de uso do REST Assured
├── OlaMundoTest.java      # Testes básicos e introdução aos Matchers
├── VerbosTest.java        # Testes dos verbos HTTP (GET, POST, PUT, DELETE)
├── UserJsonTest.java      # Testes com validação de JSON
├── UserXMLTest.java       # Testes com validação de XML
└── AuthTest.java          # Testes de autenticação
```

## 🚀 Como Executar

### Pré-requisitos

- Java 8 ou superior
- Maven 3.6 ou superior

### Instalação

1. Clone o repositório:
```bash
git clone https://github.com/bc-ferreira/curso-rest-assured.git
cd curso-rest-assured
```

2. Instale as dependências:
```bash
mvn clean install
```

### Executando os Testes

Para executar todos os testes:
```bash
mvn test
```

Para executar uma classe específica de testes:
```bash
mvn test -Dtest=OlaMundoTest
```

Para executar um método específico:
```bash
mvn test -Dtest=OlaMundoTest#testOlaMundo
```

## 📚 Conteúdo do Curso

### 1. Introdução ao REST Assured (`OlaMundo.java` e `OlaMundoTest.java`)
- Primeira requisição com REST Assured
- Validação de status code e body
- Introdução aos Matchers do Hamcrest
- Padrão Given-When-Then

### 2. Verbos HTTP (`VerbosTest.java`)
- Testes com GET, POST, PUT e DELETE
- Envio de dados no body da requisição
- Validação de diferentes status codes
- Tratamento de erros

### 3. Validação de JSON (`UserJsonTest.java`)
- Navegação em estruturas JSON
- Validação de arrays e objetos aninhados
- Extração de valores específicos
- Uso do JsonPath

### 4. Validação de XML (`UserXMLTest.java`)
- Trabalho com respostas XML
- Navegação em estruturas XML
- Validação de atributos e elementos
- Uso do XmlPath

### 5. Autenticação (`AuthTest.java`)
- Autenticação básica (Basic Auth)
- Tokens de autenticação
- Headers customizados
- Cookies e sessões

## 🔧 Exemplos de Uso

### Teste Básico
```java
@Test
public void testOlaMundo() {
    given()
    .when()
        .get("http://restapi.wcaquino.me/ola")
    .then()
        .statusCode(200)
        .body(is("Ola Mundo!"));
}
```

### Teste POST com JSON
```java
@Test
public void deveSalvarUsuario() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"José Alfredo\",\"age\":50}")
    .when()
        .post("https://restapi.wcaquino.me/users")
    .then()
        .statusCode(201)
        .body("id", is(notNullValue()))
        .body("name", is("José Alfredo"))
        .body("age", is(50));
}
```

### Validação de JSON Aninhado
```java
@Test
public void deveVerificarSegundoNivel() {
    given()
    .when()
        .get("https://restapi.wcaquino.me/users/2")
    .then()
        .statusCode(200)
        .body("name", containsString("Joaquina"))
        .body("endereco.rua", is("Rua dos bobos"));
}
```

## 📖 Recursos Úteis

- [Documentação Oficial do REST Assured](https://rest-assured.io/)
- [Hamcrest Matchers](http://hamcrest.org/JavaHamcrest/)
- [JUnit 4 Documentation](https://junit.org/junit4/)
- [Maven Getting Started](https://maven.apache.org/guides/getting-started/)

## 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto é destinado para fins educacionais.

## 👨‍💻 Autor

**Bruno Ferreira** - [bc-ferreira](https://github.com/bc-ferreira)

---

⭐ Se este projeto te ajudou, considere dar uma estrela no repositório!