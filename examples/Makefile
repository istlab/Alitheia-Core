# The Makefile.config.local should set USER to the correct
# values of username:password; it doesn't get committed to
# SVN so that's safe.
-include Makefile.config.local

# Name and password for access to the mailing list archive
USER?=bogus:notmypaswd
# Means of fetching a URL; on FreeBSD this will probably be fetch
# instead of CURL and Linux users may cordially bite my shorts.
FETCH=curl --insecure --user $(USER) 
# Raw data source to be used
WP1_LIST=https://lists.sqo-oss.eu/private/wp1/
WP3_LIST=https://lists.sqo-oss.eu/pipermail/wp3/
ALL_LIST=https://lists.sqo-oss.eu/private/all/
LIST_URL?=$(WP3_LIST)
# Directory to store munged mail in
DIR?=mail/SQO-OSS/wp3


all : squeaky-clean
	$(MAKE) fetch-mail LIST_URL=$(WP3_LIST) DIR=mail/SQO-OSS/wp3
	$(MAKE) fetch-mail LIST_URL=$(ALL_LIST) DIR=mail/SQO-OSS/all
	$(MAKE) fetch-mail LIST_URL=$(WP1_LIST) DIR=mail/SQO-OSS/wp1

fetch-mail : create-dirs update-index fetch-archives munge-archives clean

create-dirs :
	test -d tmp || mkdir tmp
	test -d $(DIR) || mkdir -p $(DIR)

update-index : fetch-index grep-index

fetch-index :
	$(FETCH) $(LIST_URL) > tmp/index.html

grep-index :
	perl -e 'while (<>) { print "$$1\n" if /href="(20[0-9][0-9]-[A-Za-z]*[^"]*gz)"/; }' < tmp/index.html > tmp/archive-files.txt

fetch-archives :
	for i in `cat tmp/archive-files.txt` ; do $(FETCH) $(LIST_URL)/$$i > tmp/$$i ; done

munge-archives :
	for i in `cat tmp/archive-files.txt` ; do gzcat tmp/$$i | perl munge.pl --dir $(DIR); done

clean :
	rm -f tmp/*

squeaky-clean : clean
	rm -rf mail/SQO-OSS/*

