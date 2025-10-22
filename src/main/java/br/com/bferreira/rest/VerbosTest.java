package br.com.bferreira.rest;
import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class VerbosTest {

    @Test
    public void deveSalvarUsuario() {
        given()
            .log().all()//exibe o log do body gerado para a request POST
            .contentType("application/json")//Para o envio de um body pelo método POST, é necessário adicionar ao given
             //o tipo de texto (json) e adicionar o body na pré-condição
            .body("{\"name\": \"José Alfredo\",\"age\":50}")
        .when()
            .post("https://restapi.wcaquino.me/users")
        .then()
            .log().all()//adiciona o log de tudo da response originada
            .statusCode(201)//201 CREATED - Foi criado um novo registro de usuario
            .body("id", is(notNullValue()))//valida que o atributo id não esteja nulo
            .body("name", is("José Alfredo"))//valida o name
            .body("age", is(50));//valida a idade
    }

    @Test
    public void naoDeveSalvarUsuarioSemNome() {
        given()
            .log().all()
            .contentType("application/json")
            .body("\"age\":50}")//não foi enviado um atributo obrigatório (name)
        .when()
            .post("https://restapi.wcaquino.me/users")
        .then()
            .log().all()
            .statusCode(400)//valida o statusCode 400 bad request
            .body("id", is(nullValue()))//valida que o atributo id está nulo
            .body("error", is("Houve algum problema no tratamento do seu XML"));//valida a mensagem de erro gerada na response do 400
    }

    @Test
    public void deveSalvarUsuarioViaXml() {
        given()
            .log().all()
            .contentType(ContentType.XML)//contentType XML usando por enum
            .body("<user><name>Maria Alberta</name><age>21</age></user>")
        .when()
            .post("https://restapi.wcaquino.me/usersXML")
        .then()
            .log().all()//adiciona o log de tudo da response originada
            .statusCode(201)//201 CREATED - Foi criado um novo registro de usuario
            .body("user.@id", is(notNullValue()))
            .body("user.name", is("Maria Alberta"))//valida o name
            .body("user.age", is("21"));
    }

    @Test
    public void deveAlterarUsuario() {//POST - url mais genérica / GET - url mais genérica/mais específica / PUT - url mais específica / DELETE - url mais específica
        given()
            .log().all()
            .contentType(ContentType.JSON)
            .body("{\"name\": \"Usuário Alterado\",\"age\":80}")
        .when()
            .put("https://restapi.wcaquino.me/users/1")//Método http alterado para PUT
        .then()
            .log().all()
            .statusCode(200)
            .body("id", is(1))
            .body("name", is("Usuário Alterado"))
            .body("age", is(80))
            .body("salary", is(1234.5678f));
    }

    @Test
    public void devoCustomizarURL() {//Parametrizar, fazer uso de parâmetros para a construção de métodos, funções ou como nesse exemplo a URL parametrizada
        given()
            .log().all()
            .contentType(ContentType.JSON)
            .body("{\"name\": \"Usuário Alterado\",\"age\":80}")
        .when()
            .put("https://restapi.wcaquino.me/{entidade}/{userId}", "users", "1")//URL parametrizada
        .then()
            .log().all()
            .statusCode(200)
            .body("id", is(1))
            .body("name", is("Usuário Alterado"))
            .body("age", is(80))
            .body("salary", is(1234.5678f));
    }

    @Test
    public void devoCustomizarURLParte2() {
        given()
            .log().all()
            .contentType(ContentType.JSON)
            .body("{\"name\": \"Usuário Alterado\",\"age\":80}")
            .pathParam("entidade", "users")//parametros passados via PathParam para a URL, a chave deve ser o mesmo nome utilizado na URL
            .pathParam("userId", "1")
        .when()
            .put("https://restapi.wcaquino.me/{entidade}/{userId}")
        .then()
            .log().all()
            .statusCode(200)
            .body("id", is(1))
            .body("name", is("Usuário Alterado"))
            .body("age", is(80))
            .body("salary", is(1234.5678f));
    }

    @Test
    public void deveRemoverUsuario() {
        given()
            .log().all()
        .when()
            .delete("https://restapi.wcaquino.me/users/1")//método DELETE para deletar o registro 1
        .then()
            .log().all()
            .statusCode(204); //204 no content / nada a declarar / foi removido
    }

    @Test
    public void naoDeveRemoverUsuarioInexistente() {
        given()
            .log().all()
        .when()
            .delete("https://restapi.wcaquino.me/users/5")
        .then()
            .log().all()
            .statusCode(400)// 400 BAD REQUEST - usuário 5 não existe - fazendo uma requisição que não pode ser atendida
            .body("error", is("Registro inexistente"));
    }
}




