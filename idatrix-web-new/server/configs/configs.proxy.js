module.exports = {
    port: 8000,
    proxy: {
        default: "http://10.0.0.84:80",
    },
    static: "./dist"
}