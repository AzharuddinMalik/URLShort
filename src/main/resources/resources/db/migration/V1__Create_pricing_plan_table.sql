-- Use BIGSERIAL for auto-incrementing primary keys in PostgreSQL
CREATE TABLE pricing_plan (
                              id BIGSERIAL PRIMARY KEY,
                              name VARCHAR(50) NOT NULL,
                              price DECIMAL(10,2) NOT NULL,
                              link_limit INT NOT NULL,
                              custom_domains BOOLEAN NOT NULL,
                              api_access BOOLEAN NOT NULL,
                              analytics BOOLEAN NOT NULL,
                              priority_support BOOLEAN NOT NULL,
                              most_popular BOOLEAN NOT NULL,
                              stripe_price_id VARCHAR(255) UNIQUE -- Add this column if not already present, and make it UNIQUE
);

-- You might want to add initial data here, replacing 'price_...' with actual Stripe Price IDs
-- INSERT INTO pricing_plan (name, price, link_limit, custom_domains, api_access, analytics, priority_support, most_popular, stripe_price_id) VALUES
-- ('Free', 0.00, 100, FALSE, FALSE, FALSE, FALSE, FALSE, 'price_12345_free_plan_id'),
-- ('Pro', 9.00, 1000, TRUE, TRUE, TRUE, TRUE, TRUE, 'price_abcde_pro_plan_id'),
-- ('Business', 29.00, 10000, TRUE, TRUE, TRUE, TRUE, FALSE, 'price_fghij_biz_plan_id');

-- Also, ensure your other tables (users, subscriptions, payments, urls) are created with PostgreSQL-compatible syntax.
-- If you're relying on Hibernate DDL-auto to create other tables, ensure your Entity.java is correct.
-- For example, for 'users' table:
/*
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
*/
-- For 'subscriptions' table:
/*
CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    plan_id BIGINT NOT NULL REFERENCES pricing_plan(id),
    status VARCHAR(50) NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    stripe_subscription_id VARCHAR(255) NOT NULL UNIQUE
);
*/
-- For 'payments' table:
/*
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    subscription_id BIGINT NOT NULL REFERENCES subscriptions(id),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(50) NOT NULL,
    stripe_payment_id VARCHAR(255) UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
*/
-- For 'urls' table (from your original project):
/*
CREATE TABLE urls (
    id BIGSERIAL PRIMARY KEY,
    long_url TEXT NOT NULL,
    short_url VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
*/
