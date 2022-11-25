new Vue({
    el: '#app',
    data: {
        products: []
    },
    created: function () {
        this.loadProducts();
    },
    methods: {
        loadProducts() {
            $.getJSON("/api/products", (data) => {
                this.products = data.data
            });
        }
    }
});
