package br.com.bferreira.rest;

import io.restassured.RestAssured;
import io.restassured.RestAssured.*;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class OlaMundoTest {

    @Test
    public void testOlaMundo() {
        //Mostra como instanciar e atribuir uma response, e depois validar essa response
        //usando asserts do Junit e a ValidatableResponse
        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
        Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
        Assert.assertTrue(response.getStatusCode() == 200);
        Assert.assertTrue("Status Code Divergente!",response.getStatusCode() == 200);
        Assert.assertEquals(200,response.getStatusCode()); // o valor esperado é o primeiro, o valor recebido o segundo

        ValidatableResponse validation = response.then();
        validation.statusCode(200);
    }

    @Test
    public void devoConhecerOutrasFormasDeTrabalharComRestAssured() {
        //Como diminuir a validação e formatar para Given, when e then. Dado, quando e então.
        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
        ValidatableResponse validation = response.then();
        validation.statusCode(200);

        RestAssured.get("http://restapi.wcaquino.me/ola").then().statusCode(200);
        RestAssured
                .given() //Pré-condições
                .when() //Ação
                    .get("http://restapi.wcaquino.me/ola")
                .then() //Assertivas
                    .statusCode(200);
    }

    @Test
    public void devoConhecerMatchersHamcrest() {
        Assert.assertThat("Maria", Matchers.is("Maria"));//assertThat atual primeiro valor, esperado o segundo
        Assert.assertThat(245, Matchers.is(245));//Verifica número
        Assert.assertThat(245, Matchers.<Integer>isA(Integer.class));//Verifica tipo
        Assert.assertThat(245, Matchers.greaterThan(200));//Verifica se é maior
        Assert.assertThat(245, Matchers.lessThan(300));//Verifica se é menor

        List<Integer> impares = Arrays.asList(1,3,5,7,9); // Lista de inteiros impares
        Assert.assertThat(impares, Matchers.<Integer>hasSize(5)); // Verifica o tamanho da lista
        Assert.assertThat(impares, Matchers.contains(1,3,5,7,9)); // Verifica se a lista contém os valores na ordem
        Assert.assertThat(impares, Matchers.containsInAnyOrder(3,1,9,7,5)); // Verifica se a lista contém os valores fora de ordem
        Assert.assertThat(impares, Matchers.hasItem(1)); // Verifica se a lista contém o valor especificado
        Assert.assertThat(impares, Matchers.hasItems(1,3,7)); // Verifica se a lista contém os valores especificados

        Assert.assertThat("Maria", Matchers.is(Matchers.not("João")));
        Assert.assertThat("Maria", Matchers.not("João")); // Valida valor divergente
        Assert.assertThat("Pedro", Matchers.anyOf(Matchers.is("Maria"), Matchers.is("Pedro"))); // Valida assertiva "ou"
        Assert.assertThat("Pedro", Matchers.allOf(Matchers.startsWith("P"), Matchers.endsWith("ro"), Matchers.containsString("e"))); // Valida caracteres da String
    }

    @Test
    public void devoValidarBody() {
        RestAssured
                .given() //Pré-condições
                .when() //Ação
                    .get("http://restapi.wcaquino.me/ola")
                .then() //Assertivas
                    .statusCode(200)
                    .body(Matchers.is("Ola Mundo!"))//Valida o body inteiro
                    .body(Matchers.containsString("Mundo"))//Valida uma parte do body
                    .body(Matchers.not(Matchers.nullValue()));//valida body not null
    }
}
