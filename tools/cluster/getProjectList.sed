{
  # match project line
 /<tr class="edit"/ {
  n;                  # skip to next line (projectID cell)
  N;                  # append next line (project name)
  s/\n\|^[\ ]*//g;    # remove line feeds and starting spaces
  s/>[\ ]*<//g;       # remove spaces between tags
  s/^.*>\(.*\)<.*&nbsp;\(.*\)<.*$/\1,\2/
  p;
  }
}
