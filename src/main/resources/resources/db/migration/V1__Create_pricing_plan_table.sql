CREATE TABLE pricing_plan (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              name VARCHAR(50) NOT NULL,
                              price DECIMAL(10,2) NOT NULL,
                              link_limit INT NOT NULL,
                              custom_domains BOOLEAN NOT NULL,
                              api_access BOOLEAN NOT NULL,
                              analytics BOOLEAN NOT NULL,
                              priority_support BOOLEAN NOT NULL,
                              most_popular BOOLEAN NOT NULL
);