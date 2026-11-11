function fn() {
    var port = karate.properties['karate.server.port'] || '8080';
    var config = {
        baseUrl: 'http://localhost:' + port
    };
    karate.configure('connectTimeout', 5000);
    karate.configure('readTimeout', 5000);
    return config;
}
