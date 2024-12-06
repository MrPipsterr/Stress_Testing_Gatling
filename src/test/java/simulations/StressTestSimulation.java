package simulations;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class StressTestSimulation extends Simulation {

    // Konfigurasi HTTP
        HttpProtocolBuilder httpProtocol = http
                .baseUrl("https://jsonplaceholder.typicode.com") // URL API yang diuji
                .acceptHeader("application/json");

        // Skenario 1: Beban Konstan
        ScenarioBuilder constantLoadScenario = scenario("Constant Load Test")
                .exec(http("Get Posts - Constant Load")
                .get("/posts")
                .check(status().is(200))); // Memastikan status HTTP 200

        // Skenario 2: Pengguna Bertahap
        ScenarioBuilder rampUsersScenario = scenario("Ramp Users Test")
                .exec(http("Get Posts - Ramp Users")
                .get("/posts")
                .check(status().is(200)));

        // Skenario 3: Lonjakan Pengguna
        ScenarioBuilder spikeLoadScenario = scenario("Spike Load Test")
                .exec(http("Get Posts - Spike Load")
                .get("/posts")
                .check(status().is(200)));

        // Pengaturan eksekusi semua skenario
        {
                setUp(
                constantLoadScenario.injectOpen(
                        constantUsersPerSec(50).during(30) // 50 pengguna/detik selama 30 detik
                ),
                rampUsersScenario.injectOpen(
                        rampUsers(200).during(20) // Tambahkan 200 pengguna selama 20 detik
                ),
                spikeLoadScenario.injectOpen(
                        nothingFor(5),         // Tunggu 5 detik
                        atOnceUsers(500),      // Tambahkan 500 pengguna sekaligus
                        rampUsers(1000).during(10) // Tambahkan 1000 pengguna dalam 10 detik
                )
                ).protocols(httpProtocol);
        }
}

