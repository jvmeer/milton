import csv
import sqlite3

def formatExpiry(expiry):
	split = expiry.split('-')
	return split[1] + '/' + split[2] + '/' + split[0][-2:]

conn = sqlite3.connect('history.db')
c = conn.cursor()
c.execute('DROP TABLE options')
c.execute('''CREATE TABLE options (identifier text, asOf text, underlier text, expiry text, strike text, optionType text,
	bidPrice real, askPrice real, bidSize real, askSize real)''')

file = open('sanjay.csv')
reader = csv.DictReader(file, delimiter=',')
for row in reader:
	identifier = row['root'] + ' ' + formatExpiry(row['expiration']) + ' ' + row['option_type'] + row['strike'] + ' Index'
	c.execute('INSERT INTO options VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)', (identifier, row['quote_date'].replace('-', ''),
		row['underlying_symbol'].replace('^', ''), row['expiration'].replace('-', ''), row['strike'], row['option_type'],
		row['bid_eod'], row['ask_eod'], row['bid_size_eod'], row['ask_size_eod']))

conn.commit()
conn.close()
file.close()

c.execute('SELECT * FROM options LIMIT 5')
print(c.fetchall())