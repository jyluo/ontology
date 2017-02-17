import os, sys, json
import fetch, fetch_corpus

# Open and parse corpus.json
WORKING_DIR = os.path.dirname(os.path.realpath(__file__))
CORPUS_INFO = None
with open(os.path.join(WORKING_DIR, 'corpus.json')) as f:
  CORPUS_INFO = json.loads(f.read())
CORPUS_DIR = os.path.join(WORKING_DIR, "corpus")

def collect_statistics():
  ontology = os.getcwd()
  with fetch_corpus.cd(CORPUS_DIR):
    for project in CORPUS_INFO['projects'].values():
      if os.path.isdir(project['name']):
        info = fetch_corpus.run_cmd([ 'bash', '../run-dljc-stats.sh', os.path.join(CORPUS_DIR, project['name']), project['build'] ])
        print info['output']

if __name__ == "__main__":
  collect_statistics()