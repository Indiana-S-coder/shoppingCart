const prompt = require('prompt-sync')({sigint: true});

class Product {
    constructor(name, price) {
        this.name = name;
        this.price = price;
    }
}

class Pair {
    constructor(discountName, discount) {
        this.discountName = discountName;
        this.discount = discount;
    }
}

class ShoppingCart {
    constructor() {
        this.products = new Map();
        this.giftWrapFee = 1;
        this.shippingFee = 5;
        this.unitsPerPackage = 10;
        this.giftWrapCharge = 0;
    }

    addProduct(product, quantity, isGiftWrap) {
        this.products.set(product, quantity);
        if (isGiftWrap) {
            this.giftWrapCharge += quantity * this.giftWrapFee;
        }
    }

    getGiftWrapFee() {
        return this.giftWrapCharge;
    }

    getShippingFee() {
        const totalShippingFee = this.shippingFee * Math.floor(this.totalUnits() / this.unitsPerPackage);
        return totalShippingFee;
    }

    subtotal() {
        let subtotal = 0;

        for (const [product, quantity] of this.products.entries()) {
            subtotal += product.price * quantity;
        }

        return subtotal;
    }

    totalUnits() {
        let totalUnits = 0;

        for (const quantity of this.products.values()) {
            totalUnits += quantity;
        }

        return totalUnits;
    }

    applyDiscount() {
        const subtotal = this.subtotal();
        const totalUnits = this.totalUnits();
        let maxQuantity = 0;
        let discount = 0;

        for (const quantity of this.products.values()) {
            if (quantity > maxQuantity) {
                maxQuantity = quantity;
            }
        }

        const flat10 = this.flat10(subtotal);
        const bulk5 = this.bulk5();
        const bulk10 = this.bulk10(subtotal, totalUnits);
        const tiered50 = this.tiered50(maxQuantity, totalUnits);

        let discountName = '';

        if (flat10 > bulk5 && flat10 > bulk10 && flat10 > tiered50) {
            discountName = 'flat_10';
            discount = flat10;
        } else if (bulk5 > flat10 && bulk5 > bulk10 && bulk5 > tiered50) {
            discountName = 'bulk_5';
            discount = bulk5;
        } else if (bulk10 > flat10 && bulk10 > bulk5 && bulk10 > tiered50) {
            discountName = 'bulk_10';
            discount = bulk10;
        } else if (tiered50 > flat10 && tiered50 > bulk5 && tiered50 > bulk10) {
            discountName = 'tiered_50';
            discount = tiered50;
        }

        return new Pair(discountName, discount);
    }

    total() {
        const total = this.subtotal();
        const discount = this.applyDiscount().discount;
        const shippingFee = this.getShippingFee();
        const giftWrapFee = this.getGiftWrapFee();

        return total - discount + shippingFee + giftWrapFee;
    }

    flat10(subtotal) {
        let discount = 0;
        if (subtotal > 200) {
            discount = 10;
        }
        return discount;
    }

    bulk5() {
        let discount = 0;
        for (const [product, quantity] of this.products.entries()) {
            if (quantity > 10) {
                discount += (product.price * quantity) * 0.05;
            }
        }
        return discount;
    }

    bulk10(subtotal, totalUnits) {
        let discount = 0;
        if (totalUnits > 20) {
            discount = subtotal * 0.1;
        }
        return discount;
    }

    tiered50(maxQuantity, totalUnits) {
        let discount = 0;

        if (totalUnits > 30 && maxQuantity > 15) {
            for (const [product, quantity] of this.products.entries()) {
                if (quantity > 15) {
                    discount += (product.price * (quantity - 15)) * 0.5;
                }
            }
        }

        return discount;
    }

}

const productA = new Product("A", 20);
const productB = new Product("B", 40);
const productC = new Product("C", 50);
const cart = new ShoppingCart();

let quantityA = prompt("Enter the quantity of Product A: ");
let isGiftWrapA = prompt("Is product A gift wrapped? Enter true or false: ");
cart.addProduct(productA, parseInt(quantityA), isGiftWrapA.toLowerCase() === 'true');

let quantityB = prompt("Enter the quantity of Product B: ");
let isGiftWrapB = prompt("Is product B gift wrapped? Enter true or false: ");
cart.addProduct(productB, parseInt(quantityB), isGiftWrapB.toLowerCase() === 'true');

let quantityC = prompt("Enter the quantity of Product C: ");
let isGiftWrapC = prompt("Is product C gift wrapped? Enter true or false: ");
cart.addProduct(productC, parseInt(quantityC), isGiftWrapC.toLowerCase() === 'true');

console.log(`Product: ${productA.name}\tQuantity: ${quantityA}\tTotal Amount: ${quantityA * productA.price}`);
console.log(`Product: ${productB.name}\tQuantity: ${quantityB}\tTotal Amount: ${quantityB * productB.price}`);
console.log(`Product: ${productC.name}\tQuantity: ${quantityC}\tTotal Amount: ${quantityC * productC.price}`);
console.log(`Subtotal: ${cart.subtotal()}`);
console.log(`Discount Name: ${cart.applyDiscount().discountName}`);
console.log(`Discount: ${cart.applyDiscount().discount}`);
console.log(`Shipping Fee: ${cart.getShippingFee()}`);
console.log(`Gift Wrap Fee: ${cart.getGiftWrapFee()}`);
console.log(`Total: ${cart.total()}`);


