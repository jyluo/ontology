import os,sys
import fetch_corpus

def main():
  # TODO: check for https://raw.githubusercontent.com/aas-integration/integration-test2/master/corpus.json file existence

  print "Fetching corpus."
  fetch_corpus.fetch_corpus()

if __name__ == "__main__":
  main()
