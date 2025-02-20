let
  pkgs = import <nixpkgs> { };
in
with pkgs;
mkShell {
  packages = [
    temurin-bin
    act
    actionlint
    nixfmt-rfc-style
    nil
  ];
}
