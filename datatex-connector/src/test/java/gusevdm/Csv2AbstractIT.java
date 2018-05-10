package gusevdm;

import gusevdm.rest.MetabaseRestClient;
import gusevdm.rest.RiverRestClient;
import joptsimple.OptionParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Delay;
import org.mockserver.model.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import static gusevdm.integration.AbstractAnswers.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * Integration tests for csv2abstract tool.
 */

public class Csv2AbstractIT {

    private static final Header DEFAULT_HEADER = new Header("Content-Type", "application/json");
    // test resources
    private static final String CSV_FILE             = "integration/verify_increment.csv";
    private static final String SCHEMA_FILE          = "integration/verify_increment.json";
    private static final String ENIGMA_CREDS_FILE    = "integration/environment_dummy.yml";

    private static final String REMOTE_HOST_FORMAT   = "http://%s:%s";
    // enigma side settings River/Metabase
    private static final String RIVER_SERVER_HOST    = "localhost";
    private static final int    RIVER_SERVER_PORT    = 1080;
    private static final String METABASE_SERVER_HOST = "localhost";
    private static final int    METABASE_SERVER_PORT = 1090;
    // abstract/river defaults
    private static final String ABS_COLLECTION_NAME  = "mycollection";
    private static final String ABS_DATASET_NAME     = ABS_COLLECTION_NAME + ".mydataset";
    private static final String ABS_COLLECTION_ID    = "977b930f-86e3-4a4d-a781-2b254013fe9b";
    private static final String ABS_DATASET_ID       = "22f80758-206a-4043-8fff-5b7b0e12db24";
    private static final int    RIVER_READ_ATTEMPTS  = 5;
    // timeout in seconds between attempts
    private static final int    RIVER_READ_TIMEOUT   = 2;

    // child class for mock up hadoop hdfs calls
    static class RiverRestClientChild extends RiverRestClient {

        private static final Logger LOGGER = LoggerFactory.getLogger(RiverRestClientChild.class);

        RiverRestClientChild(String csvFile, String schemaFile) {
            super(csvFile, schemaFile);
            LOGGER.debug("RiverRestClientChild constructor() is working. Returned from super().");
        }

        @Override
        protected InputStream openSchema() throws IOException {
            LOGGER.debug(String.format("RiverRestClientChild.openSchema() is working. Open schema file: [%s].", SCHEMA_FILE));
            return this.getClass().getClassLoader().getResourceAsStream(SCHEMA_FILE);
        }

    }

    // Mock-Server instance(s) (for river and metabase servers)
    private ClientAndServer mockRiverServer;
    private ClientAndServer mockMetabaseServer;

    private void createRiverServerClientExpectations() {

        // #1. CREATE COLLECTION: build client expectation
        new MockServerClient(RIVER_SERVER_HOST, RIVER_SERVER_PORT)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/v2/collections/"),
                        exactly(1)
                )
                .respond(
                        response()
                                .withStatusCode(201)
                                .withHeaders(DEFAULT_HEADER)
                                .withBody(String.format(ANS_ABSTRACT_CREATE_COLLECTION, ABS_COLLECTION_ID, ABS_COLLECTION_NAME))
                                .withDelay(new Delay(SECONDS, 1))
                );

        // #2. CREATE DATASET: build client expectation
        new MockServerClient(RIVER_SERVER_HOST, RIVER_SERVER_PORT)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath(String.format("/v2/collections/%s/datasets/", ABS_COLLECTION_ID)),
                        exactly(1)
                )
                .respond(
                        response()
                                .withStatusCode(201)
                                .withHeaders(DEFAULT_HEADER)
                                .withBody(String.format(ANS_ABSTRACT_CREATE_DATASET, ABS_DATASET_ID, ABS_COLLECTION_ID, ABS_DATASET_NAME, ABS_DATASET_NAME))
                                .withDelay(new Delay(SECONDS, 1))
                );

        // #3. READ DATASET STATE (AWAITING_UPLOAD): build client expectation
        new MockServerClient(RIVER_SERVER_HOST, RIVER_SERVER_PORT)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath(String.format("/v2/collections/%s/datasets/%s", ABS_COLLECTION_ID, ABS_DATASET_ID)),
                        exactly(RIVER_READ_ATTEMPTS - 1)
                ).respond(
                response()
                        .withStatusCode(200)
                        .withHeaders(DEFAULT_HEADER)
                        .withBody(String.format(ANS_ABSTRACT_DATASET_STATUS, ABS_DATASET_ID, ABS_COLLECTION_ID, "awaiting_upload"))
                        .withDelay(new Delay(SECONDS, 1))
        );

        // #4. READ DATASET STATE (INDEXED)): build client expectation
        new MockServerClient(RIVER_SERVER_HOST, RIVER_SERVER_PORT)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath(String.format("/v2/collections/%s/datasets/%s", ABS_COLLECTION_ID, ABS_DATASET_ID)),
                        exactly(1)
                ).respond(
                response()
                        .withStatusCode(200)
                        .withHeaders(DEFAULT_HEADER)
                        .withBody(String.format(ANS_ABSTRACT_DATASET_STATUS, ABS_DATASET_ID, ABS_COLLECTION_ID, "indexed"))
                        .withDelay(new Delay(SECONDS, 1))
        );

        // #5. (same as #4) READ DATASET STATE (INDEXED)): build client expectation
        new MockServerClient(RIVER_SERVER_HOST, RIVER_SERVER_PORT)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath(String.format("/v2/collections/%s/datasets/%s", ABS_COLLECTION_ID, ABS_DATASET_ID)),
                        exactly(1)
                ).respond(
                response()
                        .withStatusCode(200)
                        .withHeaders(DEFAULT_HEADER)
                        .withBody(String.format(ANS_ABSTRACT_DATASET_STATUS, ABS_DATASET_ID, ABS_COLLECTION_ID, "indexed"))
                        .withDelay(new Delay(SECONDS, 1))
        );


    }

    private void createMetabaseServerClientExpectations() {

        // #1. LOGIN TO METABASE: build client expectation
        new MockServerClient(METABASE_SERVER_HOST, METABASE_SERVER_PORT)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/signin"),
                        exactly(1)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(DEFAULT_HEADER)
                                .withBody(ANS_METABASE_LOGIN)
                                .withDelay(new Delay(SECONDS, 1))
                );

        // #2. CHECK DATASET STATUS IN METABASE: build client expectation
        new MockServerClient(METABASE_SERVER_HOST, METABASE_SERVER_PORT)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/apibase/datapath")
                                .withQueryStringParameter("datapath", ABS_DATASET_NAME),
                        exactly(2)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(DEFAULT_HEADER)
                                .withBody(String.format(ANS_METABASE_DATAPATH, ABS_DATASET_NAME))
                                .withDelay(new Delay(SECONDS, 1))
                );
        // #3. CHECK COLLECTION STATUS IN METABASE: build client expectation
        new MockServerClient(METABASE_SERVER_HOST, METABASE_SERVER_PORT)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/apibase/datapath")
                                .withQueryStringParameter("datapath", ABS_COLLECTION_NAME),
                        exactly(1)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(DEFAULT_HEADER)
                                .withBody(String.format(ANS_METABASE_DATAPATH, ABS_COLLECTION_NAME))
                                .withDelay(new Delay(SECONDS, 1))
                );
    }

    @Before
    public void startMockServers() {

        // start mock servers (River and Metabase)
        this.mockRiverServer    = startClientAndServer(RIVER_SERVER_PORT);
        this.mockMetabaseServer = startClientAndServer(METABASE_SERVER_PORT);

        // build mock servers client expectations
        this.createRiverServerClientExpectations();
        this.createMetabaseServerClientExpectations();

    }

    @After
    public void stopMockServers() {
        this.mockRiverServer.stop();
        this.mockMetabaseServer.stop();
    }

    @Test
    public void testIntegration() {

        // prepare environment properties
        System.setProperty("knox.hdfs.uri",          "/knox/hdfs/uri");
        System.setProperty("river.timeout.attempts", String.valueOf(RIVER_READ_ATTEMPTS));
        System.setProperty("river.timeout.seconds",  String.valueOf(RIVER_READ_TIMEOUT));


        // prepare cmd line
        String[] cmdLineOptions = {
                "--schema",  SCHEMA_FILE,
                "--csv",     CSV_FILE,
                "--dataset", ABS_DATASET_NAME,
                "--credentials", ENIGMA_CREDS_FILE
        };

        // prepare dependencies (river rest client and csv2abstarct module)
        Environment.load(getClass().getClassLoader().getResource(ENIGMA_CREDS_FILE).getPath());
        RiverRestClientChild riverClient = new RiverRestClientChild(CSV_FILE, SCHEMA_FILE);
        CSV2Abstract csv2Abstract        = new CSV2Abstract(ABS_DATASET_NAME, false, new MetabaseRestClient(), riverClient);
        // create Main module for csv2abstract tool
        Main csv2abstractMain            = new Main(new OptionParser(), Runtime.getRuntime());
        csv2abstractMain.setCsv2Abstract(csv2Abstract);
        // run csv2abstract tool
        csv2abstractMain.run(cmdLineOptions);

    }

}
