#!/usr/bin/env perl -w

my $usage="Usage: munge.pl --dir DIR\n";

die $usage unless $ARGV[0] eq "--dir";
my $dir = $ARGV[1];
die $usage . "\tNo such directory.\n" unless -d $dir;

my ($from,@headers,$msgid);
$from = <STDIN>;
while (<STDIN>) {
	die "Missing From line" unless $from =~ /^From /;

	$headers=$_;
	while(<STDIN>) {
		last if /^$/;
		$msgid=$_ if /^Message-ID/;
		$headers .= $_;
	}

	print $from . " " . $msgid . "\n";

	$msgid =~ s/^.*<//;
	$msgid =~ s/>.*//;
	$msgid =~ s/@.*//;
	$msgid =~ s/[^A-Za-z0-9.:]/_/g;

	my $filename = "$dir/$msgid";
	open OUTFILE,">$filename" or die "Could not create $filename\n";

	print OUTFILE $from;
	print OUTFILE $headers;
	# The blank line was swallowed in the while loop above
	print OUTFILE "\n";

	while (<STDIN>) {
		if (/^From /) {
			$from = $_;
			last;
		}
		print OUTFILE $_;
	}

	close OUTFILE;
}

