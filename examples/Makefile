# This Makefile handles fetching mail archives and unpacking them
# into maildir-style directories under here (mail/<PROJECT>/<LIST>/{new,cur,tmp}).
# It requires some configuration because the SQO-OSS list archives
# are behind an authentication barrier. This file contains default
# (and broken) username and password settings.Create a file
#	Makefile.config.local
# and put a line of the form
#	USER=<username>:<password>
# in there, with your SQO-OSS website password and username in there.
#
# By default, 'make all' will fetch the wp1, wp3 and all lists. You
# can twiddle what gets fetched by calling 'make fetch-mail' with the
# following make variables set:
#	LIST_URL
#	DIR
# Here DIR is the maildir folder to put things in; mail is placed into
# DIR/new/. The LIST_URL is the URL to the *index* page of the archives.

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
	mkdir $(DIR)/new $(DIR)/tmp $(DIR)/cur ; true

update-index : fetch-index grep-index

fetch-index :
	@$(FETCH) $(LIST_URL) > tmp/index.html

grep-index :
	perl -e 'while (<>) { print "$$1\n" if /href="(20[0-9][0-9]-[A-Za-z]*[^"]*gz)"/; }' < tmp/index.html > tmp/archive-files.txt

fetch-archives :
	@for i in `cat tmp/archive-files.txt` ; do echo "# Archive $$i"; $(FETCH) $(LIST_URL)/$$i > tmp/$$i ; done

munge-archives :
	for i in `cat tmp/archive-files.txt` ; do gzcat tmp/$$i | perl munge.pl --dir $(DIR)/new ; done

clean :
	rm -f tmp/*

squeaky-clean : clean
	rm -rf mail/SQO-OSS/*

