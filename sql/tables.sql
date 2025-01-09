CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) UNIQUE PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    coins integer DEFAULT 20
    );

CREATE TABLE IF NOT EXISTS cards (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(255) NOT NULL,
                       damage DOUBLE PRECISION NOT NULL,
                       card_type VARCHAR(50) NOT NULL CHECK (card_type IN ('SPELL', 'MONSTER')), -- Enum-ähnlich
                       user_id UUID REFERENCES users(id) ON DELETE SET NULL, -- Karte gehört eindeutig einem Benutzer, kann aber auch ohne Benutzer existieren
                       package_id UUID REFERENCES packages(id) ON DELETE SET NULL -- Karte gehört eindeutig zu einem Paket, kann aber auch ohne Paket existieren
);

CREATE TABLE IF NOT EXISTS packages (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         availability boolean DEFAULT TRUE
);
CREATE TABLE Decks (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       User_ID uuid NOT NULL,
                       Card1 uuid NOT NULL,
                       Card2 uuid NOT NULL,
                       Card3 uuid NOT NULL,
                       Card4 uuid NOT NULL,
                       FOREIGN KEY (User_ID) REFERENCES Users(ID) ON DELETE CASCADE,
                       FOREIGN KEY (Card1) REFERENCES Cards(ID) ON DELETE CASCADE,
                       FOREIGN KEY (Card2) REFERENCES Cards(ID) ON DELETE CASCADE,
                       FOREIGN KEY (Card3) REFERENCES Cards(ID) ON DELETE CASCADE,
                       FOREIGN KEY (Card4) REFERENCES Cards(ID) ON DELETE CASCADE
);