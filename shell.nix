let
  pkgs = import <nixpkgs> { };
  java = pkgs.graalvmPackages.graalvm-ce;
in
with pkgs;
mkShell {
  packages = [
    java
    act
    actionlint
    nixfmt-rfc-style
    nil
  ];

  JAVA_HOME = "${java}";
}
