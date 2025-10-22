package br.com.bferreira.rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.given;

public class UserJsonTest {

    @Test
    public void deveVerificarPrimeiroNivel() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/1")
        .then()
            .statusCode(200)
            .body("id", Matchers.is(1))
            .body("name", Matchers.containsString("Silva"))
            .body("age", Matchers.greaterThan(18));
    }

    @Test
    public void deveVerificarPrimeiroNivelOutrasFormas() {
        Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/users/1");

        //Path
        Assert.assertEquals(new Integer(1), response.path("id"));
        Assert.assertEquals(new Integer(1), response.path("%s","id"));

        //JsonPath
        JsonPath jpath = new JsonPath(response.asString());
        Assert.assertEquals(1, jpath.getInt("id"));

        // From (JsonPath)
        int id = JsonPath.from(response.asString()).getInt("id");
        Assert.assertEquals(1, id);
    }

    @Test
    public void deveVerificarSegundoNivel() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/2")
        .then()
            .statusCode(200)
            .body("name", Matchers.containsString("Joaquina"))
            .body("endereco.rua", Matchers.is("Rua dos bobos"));//Objeto endereço possui mais atributos
    }

    @Test
    public void deveVerificarLista() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/3")
        .then()
            .statusCode(200)
            .body("name", Matchers.containsString("Ana"))
            .body("filhos", Matchers.hasSize(2))
            .body("filhos[0].name", Matchers.is("Zezinho"))
            .body("filhos[1].name", Matchers.is("Luizinho"))
            .body("filhos.name", Matchers.hasItem("Zezinho"))
            .body("filhos.name", Matchers.hasItems("Zezinho", "Luizinho"));
    }

    @Test
    public void deveRetornarUsuarioInexistente() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/4")
        .then()
            .statusCode(404)
            .body("error", Matchers.is("Usuário inexistente"));
    }

    @Test
    public void deveVerificarListaNaRaiz() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/users")
        .then()
            .statusCode(200)
            .body("$", Matchers.hasSize(3))//Verifica na raiz do Json, que é uma lista
            .body("name", Matchers.hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
            .body("filhos.name", Matchers.hasItem(Arrays.asList("Zezinho", "Luizinho")))
            .body("age[1]", Matchers.is(25))
            .body("salary", Matchers.contains(1234.5678f, 2500, null));
    }

    @Test
    public void devoFazerVerificacoesAvancadas() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/users")
        .then()
            .statusCode(200)
            .body("$", Matchers.hasSize(3))
            .body("age.findAll{it <= 25}.size", Matchers.is(2))//it referencia os objetos que estão no nivel da busca
            .body("age.findAll{it <= 25 && it > 20}.size", Matchers.is(1))//é possível usar funções na string de expected do assert Equals
            .body("findAll{it.age <= 25 && it.age > 20}.name", Matchers.hasItem("Maria Joaquina"))//Obtem lista de names menores ou igual que 25 anos e maiores que 20
            .body("findAll{it.age <= 25}[0].name", Matchers.is("Maria Joaquina"))//obtem o nome no primeiro index da lista conforme condição
            .body("findAll{it.age <= 25}[-1].name", Matchers.is("Ana Júlia"))//obtem o ultimo nome da lista conforme condição [-1]
            .body("find{it.age <= 25}.name", Matchers.is("Maria Joaquina"))//findAll retorna todos o registros em uma lista, find retorna apenas o primeiro registro simples
            .body("findAll{it.name.contains('n')}.name", Matchers.hasItems("Maria Joaquina", "Ana Júlia"))//retorna lista de strings que possuem a letra 'n'
            .body("findAll{it.name.length() > 10}.name", Matchers.hasItems("João da Silva","Maria Joaquina"))//retorna lista de strings que possuem mais de 10 caracteres
            .body("name.collect{it.toUpperCase()}", Matchers.hasItem("MARIA JOAQUINA"))//retorna uma lista de names que it referencia o atributo name de cada objeto e transforma todos as letras para maíusculas
            .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", Matchers.hasItem("MARIA JOAQUINA"))//retorna a partir do atributo name apenas o que inicia com a string Maria e transforma todos as letras para maíusculas
            .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", Matchers.allOf(Matchers.arrayContaining("MARIA JOAQUINA"), Matchers.arrayWithSize(1)))//Duas validações do array
            .body("age.collect{it * 2}", Matchers.hasItems(60, 50, 40))// retorna uma lista com os atributos age multiplicados por 2 e os valida
            .body("id.max()", Matchers.is(3))// retorna o maior id do json e valida
            .body("salary.min()", Matchers.is(1234.5678f))// retorna o menor salário do json e valida
            .body("salary.findAll{it != null}.sum()", Matchers.is(Matchers.closeTo(3734.5678f, 0.001)))//soma todos os atributos salário do Json filtrando os que não são null
            .body("salary.findAll{it != null}.sum()", Matchers.allOf(Matchers.greaterThan(3000d), Matchers.lessThan(5000d)));//soma todos os atributos salário do Json filtrando os que não são null
    }

    @Test
    public void devoUnirJsonPathComJava() {
        ArrayList<String> names = //Passa uma lista que contem o valor do atributo name que inicia com 'Maria' para um ArrayList
            given()
            .when()
                .get("https://restapi.wcaquino.me/users")
            .then()
                .statusCode(200)
                .extract().path("name.findAll{it.startsWith('Maria')}");
        Assert.assertEquals(1, names.size());
        Assert.assertTrue(names.get(0).equalsIgnoreCase("mArIa JoAqUiNa"));//Faz a assertiva independente do case dos caracteres da string
        Assert.assertEquals(names.get(0).toUpperCase(), "maria joaquina".toUpperCase());
    }
}
