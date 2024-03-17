-- Create the Expression table
CREATE TABLE Expression
(
    id         SERIAL PRIMARY KEY,
    expression TEXT NOT NULL
);

-- Create the Roots table with an index on the value column
CREATE TABLE Root
(
    id    SERIAL PRIMARY KEY,
    value NUMERIC(17, 10) NOT NULL
);

-- Create an index on the value column in the Roots table
CREATE INDEX idx_value ON Root (value);

-- Create the Expression_Root junction table
CREATE TABLE Expression_Root
(
    expression_id INTEGER REFERENCES Expression (id),
    root_id       INTEGER REFERENCES Root (id),
    PRIMARY KEY (expression_id, root_id)
);