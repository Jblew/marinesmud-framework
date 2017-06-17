/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.crypto;

import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.xml.bind.DatatypeConverter;

/**
 * Source: https://github.com/defuse/password-hashing
 * @author Defuse
 * 
 * Secure Password Storage v2.0 =============================
 *
 * [![Build
 * Status](https://travis-ci.org/defuse/password-hashing.svg?branch=master)](https://travis-ci.org/defuse/password-hashing)
 *
 * This repository contains peer-reviewed libraries for password storage in PHP,
 * C#, Ruby, and Java. Passwords are "hashed" with PBKDF2 (64,000 iterations of
 * SHA1 by default) using a cryptographically-random salt. The implementations
 * are compatible with each other, so you can, for instance, create a hash in
 * PHP and then verify it in C#.
 *
 * Should you use this code? --------------------------
 *
 * This code uses the PBKDF2 algorithm to protect passwords. Better technologies
 * for protecting passwords exist today, like bcrypt, scrypt, or Argon2. Before
 * using this code, you should try to find a well-reviewed and carefully-made
 * implementation of one of those algorithms for the language that you are
 * using. These algorithms are "memory hard," meaning that they don't just need
 * a lot of CPU power to compute, they also require a lot of memory (unlike
 * PBKDF2). By using a memory hard algorithm, your passwords will be better
 * protected.
 *
 * One thing you could do would be to use
 * [libsodium](https://github.com/jedisct1/libsodium) to [hash your passwords
 * with scrypt](https://download.libsodium.org/doc/password_hashing/index.html).
 * It has bindings available for many languages.
 *
 * Since there are better options, this code is now in "maintenance mode." Only
 * bugs will be fixed, no new features will be added. It is currently safe to
 * use, but using libsodium would be better.
 *
 * Usage ------
 *
 * You should not store users' passwords in plain text on your servers. Nor
 * should you even store them in encrypted form. The correct way to store a
 * password is to store something created from the password, which we'll call a
 * "hash." Hashes don't allow you to recover the password, they only let you
 * check if a password is the same as the one that created the hash.
 *
 * There are a lot of subtle details about password hashing that this library
 * hides from you. You don't need to worry about things like "salt" with this
 * library. It takes care of all of that for you.
 *
 * To implement a user login system, you need two parts: creating new accounts,
 * and logging in to existing accounts. When you create a new account, your code
 * will create a hash of the new account's password and save it somewhere. When
 * you log in to an account, your code will use the hash to check if the login
 * password is correct.
 *
 * To create a hash, when a new account is added to your system, you call the
 * `CreateHash()` method provided by this library. To verify a password, you
 * call `VerifyPassword()` method provided by this library.
 *
 * Here is more specific documentation for both functions. The behavior should
 * be the same for all of the implementations (although the method names differ
 * slightly). If one implementation behaves differently than another, that is a
 * bug, and should be filed in the GitHub issue tracker.
 *
 * ### CreateHash(password)
 *
 **Preconditions:**
 *
 * - You're intending to create a new account, or the password to an existing
 * account is being changed. - `password` is the password for the new account,
 * or the new password for an existing account.
 *
 **Postconditions:**
 *
 * - `CreateHash()` gives you a string which can be used with `VerifyPassword()`
 * to check, in the future, if a password is the same as the `password` given to
 * this call.
 *
 **Obligations:**
 *
 * - Store the string `CreateHash()` returns to you in a safe place. If an
 * attacker can modify your hashes, they will be able to change them to, for
 * instance, the hash of "1234", and then log in to any account. If an attacker
 * can view your hashes, they can begin cracking them (by trying to
 * guess-and-check passwords).
 *
 **Exceptions:**
 *
 * - `CannotPerformOperationException`: If this exception is thrown, it means
 * something is wrong with the platform your code is running on, and it's not
 * safe to create a hash. For example, if your system's random number generator
 * doesn't work properly, this kind of exception will be thrown.
 *
 * ### VerifyPassword(password, correctHash)
 *
 **Preconditions:**
 *
 * - Someone is logging in to a user account which has been created in the past.
 * - `password` is the password provided by the person trying to log in. -
 * `correctHash` is the hash of the account's correct password, made with
 * `CreateHash()` when the account was created or when its password was last
 * changed. Make sure you are providing the hash for the correct user account! -
 * `correctHash` hasn't been seen by or changed by an attacker since it was
 * created.
 *
 **Postconditions:**
 *
 * - True is returned if the password provided by the person logging in is
 * correct. False is returned if not.
 *
 **Obligations:**
 *
 * - Make sure the `correctHash` you're giving is for the right account. If you
 * give a hash for the wrong account, it would let someone log into Alice's
 * account using Bob's password!
 *
 **Exceptions:**
 *
 * - `CannotPerformOperationException`: If this exception is thrown, it means
 * something is wrong with the platform your code is running on, and for some
 * reason it's not safe to verify a password on it. - `InvalidHashException`:
 * The `correctHash` you gave was somehow corrupted. Note that some ways of
 * corrupting a hash are impossible to detect, and their only symptom will be
 * that `VerifyPassword()` will return false even though the correct password
 * was given. So `InvalidHashException` is not guaranteed to be thrown if a hash
 * has been changed, but *if it is thrown* then you can be sure that the hash
 * was changed.
 *
 * Customization --------------
 *
 * Each implementation provides several constants that can be changed. **Only
 * change these if you know what you are doing, and have help from an expert**:
 *
 * - `PBKDF2_HASH_ALGORITHM`: The hash function PBKDF2 uses. By default, it is
 * SHA1 for compatibility across implementations, but you may change it to
 * SHA256 if you don't care about compatibility. Although SHA1 has been
 * cryptographically broken as a collision-resistant function, it is still
 * perfectly safe for password storage with PBKDF2.
 *
 * - `PBKDF2_ITERATIONS`: The number of PBKDF2 iterations. By default, it is
 * 32,000. To provide greater protection of passwords, at the expense of needing
 * more processing power to validate passwords, increase the number of
 * iterations. The number of iterations should not be decreased.
 *
 * - `PBKDF2_SALT_BYTES`: The number of bytes of salt. By default, 24 bytes,
 * which is 192 bits. This is more than enough. This constant should not be
 * changed.
 *
 * - `PBKDF2_HASH_BYTES`: The number of PBKDF2 output bytes. By default, 18
 * bytes, which is 144 bits. While it may seem useful to increase the number of
 * output bytes, doing so can actually give an advantage to the attacker, as it
 * introduces unnecessary (avoidable) slowness to the PBKDF2 computation. 144
 * bits was chosen because it is (1) Less than SHA1's 160-bit output (to avoid
 * unnecessary PBKDF2 overhead), and (2) A multiple of 6 bits, so that the
 * base64 encoding is optimal.
 *
 * Note that these constants are encoded into the hash string when it is created
 * with `CreateHash` so that they can be changed without breaking existing
 * hashes. The new (changed) values will apply only to newly-created hashes.
 *
 * Hash Format ------------
 *
 * The hash format is five fields separated by the colon (':') character.
 *
 * ``` algorithm:iterations:hashSize:salt:hash ```
 *
 * Where:
 *
 * - `algorithm` is the name of the cryptographic hash function ("sha1"). -
 * `iterations` is the number of PBKDF2 iterations ("64000"). - `hashSize` is
 * the length, in bytes, of the `hash` field (after decoding). - `salt` is the
 * salt, base64 encoded. - `hash` is the PBKDF2 output, base64 encoded. It must
 * encode `hashSize` bytes.
 *
 * Here are some example hashes (all of the password "foobar"):
 *
 * ``` sha1:64000:18:B6oWbvtHvu8qCgoE75wxmvpidRnGzGFt:R1gkPOuVjqIoTulWP1TABS0H
 * sha1:64000:18:/GO9XQOPexBFVzRjC9mcOkVEi7ZHQc0/:0mY83V5PvmkkHRR41R1iIhx/
 * sha1:64000:18:rxGkJ9fMTNU7ezyWWqS7QBOeYKNUcVYL:tn+Zr/xo99LI+kSwLOUav72X
 * sha1:64000:18:lFtd+Qf93yfMyP6chCxJP5nkOxri6Zbh:B0awZ9cDJCTdfxUVwVqO+Mb5 ```
 *
 * The hash length in bytes is included to prevent an accident where the hash
 * gets truncated. For instance, if the hash were stored in a database column
 * that wasn't big enough, and the database was configured to truncate it, the
 * result when the hash gets read back would be an easy-to-break hash, since the
 * PBKDF2 output is right at the end. Therefore, the length of the hash should
 * not be determined solely from the length of the last field; it must be
 * compared against the stored length.
 *
 * More Information -----------------
 *
 * For more information on secure password storage, see [Crackstation's page on
 * Password Hashing Security](https://crackstation.net/hashing-security.htm).
 */
public class PasswordStorage
{

    @SuppressWarnings("serial")
    static public class InvalidHashException extends Exception {
        public InvalidHashException(String message) {
            super(message);
        }
        public InvalidHashException(String message, Throwable source) {
            super(message, source);
        }
    }

    @SuppressWarnings("serial")
    static public class CannotPerformOperationException extends Exception {
        public CannotPerformOperationException(String message) {
            super(message);
        }
        public CannotPerformOperationException(String message, Throwable source) {
            super(message, source);
        }
    }

    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

    // These constants may be changed without breaking existing hashes.
    public static final int SALT_BYTE_SIZE = 24;
    public static final int HASH_BYTE_SIZE = 18;
    public static final int PBKDF2_ITERATIONS = 64000;

    // These constants define the encoding and may not be changed.
    public static final int HASH_SECTIONS = 5;
    public static final int HASH_ALGORITHM_INDEX = 0;
    public static final int ITERATION_INDEX = 1;
    public static final int HASH_SIZE_INDEX = 2;
    public static final int SALT_INDEX = 3;
    public static final int PBKDF2_INDEX = 4;

    public static String createHash(String password)
        throws CannotPerformOperationException
    {
        return createHash(password.toCharArray());
    }

    public static String createHash(char[] password)
        throws CannotPerformOperationException
    {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTE_SIZE];
        random.nextBytes(salt);

        // Hash the password
        byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);
        int hashSize = hash.length;

        // format: algorithm:iterations:hashSize:salt:hash
        String parts = "sha1:" +
            PBKDF2_ITERATIONS +
            ":" + hashSize +
            ":" +
            toBase64(salt) +
            ":" +
            toBase64(hash);
        return parts;
    }

    public static boolean verifyPassword(String password, String correctHash)
        throws CannotPerformOperationException, InvalidHashException
    {
        return verifyPassword(password.toCharArray(), correctHash);
    }

    public static boolean verifyPassword(char[] password, String correctHash)
        throws CannotPerformOperationException, InvalidHashException
    {
        // Decode the hash into its parameters
        String[] params = correctHash.split(":");
        if (params.length != HASH_SECTIONS) {
            throw new InvalidHashException(
                "Fields are missing from the password hash."
            );
        }

        // Currently, Java only supports SHA1.
        if (!params[HASH_ALGORITHM_INDEX].equals("sha1")) {
            throw new CannotPerformOperationException(
                "Unsupported hash type."
            );
        }

        int iterations = 0;
        try {
            iterations = Integer.parseInt(params[ITERATION_INDEX]);
        } catch (NumberFormatException ex) {
            throw new InvalidHashException(
                "Could not parse the iteration count as an integer.",
                ex
            );
        }

        if (iterations < 1) {
            throw new InvalidHashException(
                "Invalid number of iterations. Must be >= 1."
            );
        }


        byte[] salt = null;
        try {
            salt = fromBase64(params[SALT_INDEX]);
        } catch (IllegalArgumentException ex) {
            throw new InvalidHashException(
                "Base64 decoding of salt failed.",
                ex
            );
        }

        byte[] hash = null;
        try {
            hash = fromBase64(params[PBKDF2_INDEX]);
        } catch (IllegalArgumentException ex) {
            throw new InvalidHashException(
                "Base64 decoding of pbkdf2 output failed.",
                ex
            );
        }


        int storedHashSize = 0;
        try {
            storedHashSize = Integer.parseInt(params[HASH_SIZE_INDEX]);
        } catch (NumberFormatException ex) {
            throw new InvalidHashException(
                "Could not parse the hash size as an integer.",
                ex
            );
        }

        if (storedHashSize != hash.length) {
            throw new InvalidHashException(
                "Hash length doesn't match stored hash length."
            );
        }

        // Compute the hash of the provided password, using the same salt, 
        // iteration count, and hash length
        byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return slowEquals(hash, testHash);
    }

    private static boolean slowEquals(byte[] a, byte[] b)
    {
        int diff = a.length ^ b.length;
        for(int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
        throws CannotPerformOperationException
    {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException ex) {
            throw new CannotPerformOperationException(
                "Hash algorithm not supported.",
                ex
            );
        } catch (InvalidKeySpecException ex) {
            throw new CannotPerformOperationException(
                "Invalid key spec.",
                ex
            );
        }
    }

    private static byte[] fromBase64(String hex)
        throws IllegalArgumentException
    {
        return DatatypeConverter.parseBase64Binary(hex);
    }

    private static String toBase64(byte[] array)
    {
        return DatatypeConverter.printBase64Binary(array);
    }

}