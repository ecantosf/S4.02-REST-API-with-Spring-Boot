// init-mongo.js - Script d'inicialització per a MongoDB

// Crear base de dades si no existeix
db = db.getSiblingDB('orderdb');

// Crear col·lecció orders
db.createCollection('orders');

// Crear índexs per millorar el rendiment
db.orders.createIndex({ "client_name": 1 });
db.orders.createIndex({ "delivery_date": 1 });
db.orders.createIndex({ "client_name": "text" });

// Crear usuari per a l'aplicació
db.createUser({
  user: 'orderuser',
  pwd: 'orderpass',
  roles: [
    { role: 'readWrite', db: 'orderdb' }
  ]
});

print('MongoDB initialized successfully!');