#!/usr/bin/env sh
set -eu

usage() {
  cat <<'USAGE'
Usage:
  scripts/init-template.sh --service-name SERVICE --package PACKAGE [options]

Required:
  --service-name NAME       Runtime service name, for example orders-service.
  --package PACKAGE         Java package, for example com.acme.orders.

Options:
  --group-id GROUP          Maven groupId. Defaults to the first two package parts.
  --root-artifact-id ID     Root Maven artifactId. Defaults to SERVICE.
  --app-artifact-id ID      App module artifactId. Defaults to SERVICE-app.
  --dry-run                 Print planned changes without modifying files.
  -h, --help                Show this help.

Example:
  scripts/init-template.sh \
    --service-name orders-service \
    --package com.acme.orders \
    --group-id com.acme
USAGE
}

service_name=""
package_name=""
group_id=""
root_artifact_id=""
app_artifact_id=""
dry_run="false"

while [ "$#" -gt 0 ]; do
  case "$1" in
    --service-name)
      service_name="${2:-}"
      shift 2
      ;;
    --package)
      package_name="${2:-}"
      shift 2
      ;;
    --group-id)
      group_id="${2:-}"
      shift 2
      ;;
    --root-artifact-id)
      root_artifact_id="${2:-}"
      shift 2
      ;;
    --app-artifact-id)
      app_artifact_id="${2:-}"
      shift 2
      ;;
    --dry-run)
      dry_run="true"
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      usage >&2
      exit 2
      ;;
  esac
done

if [ -z "$service_name" ] || [ -z "$package_name" ]; then
  echo "--service-name and --package are required." >&2
  usage >&2
  exit 2
fi

case "$service_name" in
  *[!a-z0-9-]*|""|-*|*-)
    echo "--service-name must use lowercase letters, numbers, and dashes." >&2
    exit 2
    ;;
esac

if [ -z "$package_name" ]; then
  echo "--package must not be empty." >&2
  exit 2
fi

case "$package_name" in
  *[!a-zA-Z0-9_.]*|.*|*.|*..*)
    echo "--package must be a valid dotted Java package." >&2
    exit 2
    ;;
esac

case "$package_name" in
  *.*)
    ;;
  *)
    echo "--package must contain at least two parts, for example com.acme.orders." >&2
    exit 2
    ;;
esac

if [ -z "$group_id" ]; then
  first_part=$(printf '%s' "$package_name" | cut -d. -f1)
  second_part=$(printf '%s' "$package_name" | cut -d. -f2)
  group_id="${first_part}.${second_part}"
fi

if [ -z "$root_artifact_id" ]; then
  root_artifact_id="$service_name"
fi

if [ -z "$app_artifact_id" ]; then
  app_artifact_id="${service_name}-app"
fi

old_package="com.example.service"
old_package_path="com/example/service"
new_package_path=$(printf '%s' "$package_name" | tr . /)
old_service_name="service-template"
old_db_name="service_template"
new_db_name=$(printf '%s' "$service_name" | tr - _)

print_plan() {
  cat <<PLAN
Template initialization plan:
  service name:       $old_service_name -> $service_name
  package:            $old_package -> $package_name
  package path:       $old_package_path -> $new_package_path
  groupId:            com.example -> $group_id
  root artifactId:    lightweight-java-service-template -> $root_artifact_id
  app artifactId:     service-template-app -> $app_artifact_id
  local database:     $old_db_name -> $new_db_name
PLAN
}

print_plan

if [ "$dry_run" = "true" ]; then
  exit 0
fi

replace_in_file() {
  file="$1"
  if [ -f "$file" ]; then
    sed -i.bak \
      -e "s/$old_package/$package_name/g" \
      -e "s/$old_package_path/$new_package_path/g" \
      -e "s/$old_service_name/$service_name/g" \
      -e "s/lightweight-java-service-template/$root_artifact_id/g" \
      -e "s/service-template-app/$app_artifact_id/g" \
      -e "s/com.example/$group_id/g" \
      -e "s/$old_db_name/$new_db_name/g" \
      "$file"
    rm "$file.bak"
  fi
}

for file in $(git ls-files); do
  case "$file" in
    scripts/init-template.sh)
      ;;
    *.java|*.proto|*.xml|*.md|*.yml|*.yaml|*.txt|*.properties|*.example|Dockerfile|Makefile|docker-compose.yml|.env.example)
      replace_in_file "$file"
      ;;
  esac
done

move_package_root() {
  root="$1"
  old_dir="$root/$old_package_path"
  new_dir="$root/$new_package_path"

  if [ -d "$old_dir" ]; then
    mkdir -p "$(dirname "$new_dir")"
    mv "$old_dir" "$new_dir"
    cleanup="$root/com/example"
    while [ "$cleanup" != "$root" ]; do
      rmdir "$cleanup" 2>/dev/null || break
      cleanup=$(dirname "$cleanup")
    done
  fi
}

move_package_root "app/src/main/java"
move_package_root "app/src/test/java"

cat <<'NEXT'

Initialization complete.

Recommended next commands:
  make format
  make test

Review README.md, docs/, AGENTS.md, and .agents/ for any wording your service should own.
NEXT
