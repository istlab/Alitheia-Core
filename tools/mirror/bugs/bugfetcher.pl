use strict;

use Getopt::Std;
use LWP::Simple;

our $opt_c;    
my %config;

getopt("c");

if (!$opt_c) {
    print(STDERR "Usage: perl bugfetcher.pl -c config_file\n");
    exit 1;
}

open(CONFIG, "< $opt_c");

while (<CONFIG>) {
    chomp;                  # no newline
    s/#.*//;                # no comments
    s/^\s+//;               # no leading white
    s/\s+$//;               # no trailing white
    next unless length;     # anything left?
    my ($var, $value) = split(/\s*=\s*/, $_, 2);
    $config{$var} = $value;
}

close(CONFIG);

my $query = $config{'url'} . '?' . 'id=' . $config{'id'};
my $content = get($query);

if ($content =~ /<bug error="NotFound">/) {
    exit 1;
}

open(CONFIG, "> $config{'config'}");
open(OUT, ">> $config{'out'}");
open(LOG, ">> $config{'log'}");

print(OUT $content);

print(CONFIG "config=$config{'config'}\n");
print(CONFIG "log=$config{'log'}\n");
print(CONFIG "out=$config{'out'}\n");
print(CONFIG "url=$config{'url'}\n");
my $next_id = $config{'id'} + 1;
print(CONFIG "id=$next_id\n");

my $now = localtime();
print(LOG "$now $config{'id'}\n");

close(NEXT);
close(OUT);
close(LOG);
