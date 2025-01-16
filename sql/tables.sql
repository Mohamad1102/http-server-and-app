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

CREATE TABLE IF NOT EXISTS trading_deals (
                                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    card_to_trade UUID REFERENCES cards(id) ON DELETE CASCADE,  -- Die Karte, die der Spieler anbieten möchte
    trade_type VARCHAR(50) CHECK (trade_type IN ('monster', 'spell')),  -- Der Typ der Karte (Monster oder Spell)
    minimum_damage DOUBLE PRECISION,  -- Der Mindestschaden, den die Handelskarte haben muss
    user_id UUID REFERENCES users(id) ON DELETE CASCADE  -- Der Spieler, der das Handelsangebot erstellt
);

CREATE TABLE IF NOT EXISTS completed_trades (
                                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    trading_deal_id UUID REFERENCES trading_deals(id) ON DELETE CASCADE,  -- Das Handelsangebot, das akzeptiert wurde
    offering_user_id UUID REFERENCES users(id) ON DELETE CASCADE,  -- Der Spieler, der das Handelsangebot gemacht hat
    accepting_user_id UUID REFERENCES users(id) ON DELETE CASCADE,  -- Der Spieler, der das Angebot angenommen hat
    trade_card UUID REFERENCES cards(id) ON DELETE CASCADE,  -- Die Karte, die vom akzeptierenden Spieler gegeben wurde
    trade_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Das Datum, an dem der Handel abgeschlossen wurde
);

CREATE TABLE IF NOT EXISTS stats (
    userid UUID REFERENCES users(id) ON DELETE CASCADE,
    numberOfBattles int,
    wins int,
    losses int
);


CREATE TABLE IF NOT EXISTS Elo (
    userID UUID REFERENCES users(id) ON DELETE CASCADE,
    EloRating int DEFAULT 100
    );

TRUNCATE users CASCADE;
TRUNCATE cards CASCADE;
TRUNCATE packages CASCADE;
TRUNCATE decks CASCADE;
TRUNCATE elo CASCADE;
TRUNCATE stats CASCADE;
TRUNCATE trading_deals CASCADE;


